@echo off
setlocal enabledelayedexpansion
title Bwana - launcher

rem ---------------------------------------------------------------------------
rem  Starts the Lost City server (if it is not already up) and then the client.
rem  Double-click this file to play.
rem
rem  Why a script rather than just double-clicking the jar:
rem    - the client needs four arguments, and with none it prints usage and exits
rem    - it must run on a JDK 8, not the 32-bit system JRE on PATH
rem    - the server has to be listening first, or the client sits on
rem      "Error loading - will retry in NN secs"
rem
rem  Nothing here is machine-specific. The JDK 8 and the server are both searched
rem  for, and either can be named outright if the search does not find yours:
rem
rem    set BWANA_JDK8=C:\path\to\jdk8
rem    set BWANA_SERVER=C:\path\to\Server
rem ---------------------------------------------------------------------------

set "ROOT=%~dp0"
set "BUN=%USERPROFILE%\.bun\bin\bun.exe"
set "PROBE=%TEMP%\bwana-jdk-probe.txt"

rem --- which revision -----------------------------------------------------------

rem  REV, OFFSET, WEBPORT, GAMEPORT, CLIENTARGS, JAR and SERVER all come from
rem  revision.cmd, so the ports this waits on and the ports setup-server.cmd wrote
rem  into the engine's .env cannot drift apart.
call "%ROOT%revision.cmd" %1
if errorlevel 1 (
  pause
  exit /b 1
)

set "ENGINE="
if defined SERVER set "ENGINE=%SERVER%\engine"

rem --- which JDK 8 --------------------------------------------------------------

rem  BWANA_JDK8 wins, then the portable JDK the work PC uses, then a search of the
rem  usual install roots. Only a directory whose java reports version 8 counts,
rem  which is what keeps this off both the 32-bit system JRE on PATH and a newer
rem  JDK that happens to be installed alongside.

set "JDK8="
if defined BWANA_JDK8 if exist "%BWANA_JDK8%\bin\java.exe" set "JDK8=%BWANA_JDK8%"
if not defined JDK8 if exist "%LOCALAPPDATA%\jdks\jdk8u502-b07\bin\java.exe" set "JDK8=%LOCALAPPDATA%\jdks\jdk8u502-b07"

if not defined JDK8 (
  for /d %%D in ("%LOCALAPPDATA%\jdks\*")             do call :consider8 "%%~fD"
  for /d %%D in ("%USERPROFILE%\.jdks\*")             do call :consider8 "%%~fD"
  for /d %%D in ("%ProgramFiles%\Java\*")             do call :consider8 "%%~fD"
  for /d %%D in ("%ProgramFiles%\Eclipse Adoptium\*") do call :consider8 "%%~fD"
  for /d %%D in ("%ProgramFiles%\Microsoft\*")        do call :consider8 "%%~fD"
  for /d %%D in ("%ProgramFiles%\Amazon Corretto\*")  do call :consider8 "%%~fD"
  for /d %%D in ("%ProgramFiles%\Zulu\*")             do call :consider8 "%%~fD"
)
if exist "%PROBE%" del "%PROBE%" >nul 2>&1

if not exist "%JAR%" (
  echo.
  echo   Client jar not found:
  echo   %JAR%
  echo.
  echo   Run build-home.cmd first.
  echo.
  pause
  exit /b 1
)

if not defined JDK8 (
  echo.
  echo   No JDK 8 found. Looked under:
  echo.
  echo     %LOCALAPPDATA%\jdks\
  echo     %USERPROFILE%\.jdks\
  echo     %ProgramFiles%\Java\ ^(and Adoptium, Microsoft, Corretto, Zulu^)
  echo.
  echo   Install one, or point at the one you have:
  echo.
  echo     set BWANA_JDK8=C:\path\to\jdk8
  echo.
  pause
  exit /b 1
)

rem --- server -----------------------------------------------------------------

netstat -ano | findstr /r /c:":%GAMEPORT% .*LISTENING" >nul 2>&1
if not errorlevel 1 (
  echo Server already running.
  goto client
)

if not defined ENGINE (
  echo.
  echo   No rev %REV% server here. It is a separate project and is not vendored
  echo   in this repo. Looked for engine\src\app.ts under:
  echo.
  echo     %ROOT%..\Server-%REV%\
  echo     %ROOT%Server-%REV%\
  echo.
  echo   Fetch it:
  echo.
  echo     setup-server.cmd %REV%
  echo.
  echo   or point at an existing copy:
  echo.
  echo     set BWANA_SERVER=C:\path\to\Server
  echo.
  echo   Only play.cmd needs the server. run-client.cmd starts the client alone.
  echo.
  pause
  exit /b 1
)

if not exist "%BUN%" (
  echo.
  echo   Bun not found at %BUN%
  echo   The server runs on it and cannot start without it. Install with:
  echo.
  echo     powershell -c "irm bun.sh/install.ps1 ^| iex"
  echo.
  echo   That installs to %USERPROFILE%\.bun\bin\, which is where this looks.
  echo.
  pause
  exit /b 1
)

echo Starting server...
rem Own console window, not minimised: if the server fails, the reason is on
rem screen instead of vanishing. Its output also goes to Server\engine\server.log.
pushd "%ENGINE%"
start "Lost City server" "%BUN%" run src/app.ts
popd

echo Waiting for the world to load. This takes about a minute.
set /a TRIES=0
:wait
rem ping, not timeout: timeout needs a real console input handle and fails
rem outright when the script is launched without one, which made this loop spin
rem through instantly and give up.
ping -n 3 127.0.0.1 >nul
netstat -ano | findstr /r /c:":%WEBPORT% .*LISTENING" >nul 2>&1
if not errorlevel 1 goto ready
set /a TRIES+=1
if %TRIES% lss 60 goto wait

echo.
echo   The server did not come up in time.
echo   Look at the "Lost City server" window for the reason.
echo.
pause
exit /b 1

:ready
echo Server ready.

rem --- client -----------------------------------------------------------------

:client
echo Starting client...
rem java.exe rather than javaw, so the process owns a console at all. Note the
rem window is minimised and nothing is redirected, so a stack trace thrown during
rem startup still goes with the window when the process dies: use run-client.cmd,
rem which runs the same command in the foreground, to read it.
rem
rem JVM flags exist to stop the game loop freezing for a third of a second at a
rem time. JDK 8 defaults to the parallel collector, whose full collections are
rem stop-the-world and land squarely in the 300-700ms range -- which is exactly
rem what the client was doing every few seconds in Varrock, where model and map
rem loading churns the heap hardest. It reads as a disconnect but the connection
rem is never lost; the loop simply stops.
rem   -Xms = -Xmx   fixed heap, so no pause is spent growing it
rem   UseG1GC       incremental collector that works to a pause target
rem   MaxGCPauseMillis  40ms, i.e. under two game ticks
set "JVM=-Xms1536m -Xmx1536m -XX:+UseG1GC -XX:MaxGCPauseMillis=40"
pushd "%CLIENT%"
start "Bwana client %REV%" /MIN "%JDK8%\bin\java.exe" %JVM% -jar "%JAR%" %CLIENTARGS%
popd
exit /b 0

rem --- subroutines -------------------------------------------------------------

:consider8
rem  %1 = a candidate JDK directory. Takes the first one reporting version 8.
if defined JDK8 exit /b
call :probe8 "%~1"
if "%VER%"=="8" set "JDK8=%~1"
exit /b

:probe8
rem  %1 = a candidate directory. Sets VER to the major version its java reports,
rem  or leaves it empty. The version goes through a file rather than a pipe
rem  because java prints -version to stderr, and the quoting needed to capture
rem  that inline breaks on the spaces in "Program Files".
set "VER="
set "RAW="
if not exist "%~1\bin\java.exe" exit /b
"%~1\bin\java.exe" -version > "%PROBE%" 2>&1
if errorlevel 1 exit /b
for /f "tokens=3" %%V in ('findstr /i "version" "%PROBE%"') do set "RAW=%%~V"
if not defined RAW exit /b
rem  8 reports 1.8.0_502, everything since reports 17.0.9 or plain 21.
for /f "tokens=1,2 delims=." %%a in ("%RAW%") do (
  if "%%a"=="1" (set "VER=%%b") else (set "VER=%%a")
)
exit /b
