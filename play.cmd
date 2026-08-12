@echo off
setlocal
title Bwana - launcher

rem ---------------------------------------------------------------------------
rem  Starts the Lost City server (if it is not already up) and then the client.
rem  Double-click this file to play.
rem
rem  Why a script rather than just double-clicking the jar:
rem    - the client needs four arguments, and with none it prints usage and exits
rem    - it must run on the portable JDK 8, not the 32-bit system JRE on PATH
rem    - the server has to be listening first, or the client sits on
rem      "Error loading - will retry in NN secs"
rem ---------------------------------------------------------------------------

set "ROOT=%~dp0"
set "JDK8=%LOCALAPPDATA%\jdks\jdk8u502-b07"
set "BUN=%USERPROFILE%\.bun\bin\bun.exe"
set "JAR=%ROOT%Client-Java\build\libs\rs2client.jar"
set "ENGINE=%ROOT%Server\engine"

rem The client derives both ports from one offset: http = 80 + offset,
rem game = 43594 + offset. Port 80 is taken on this machine, hence 2000.
set "ARGS=10 2000 highmem members"
set "WEBPORT=2080"
set "GAMEPORT=45594"

if not exist "%JAR%" (
  echo.
  echo   Client jar not found:
  echo   %JAR%
  echo.
  echo   Run build.cmd first.
  echo.
  pause
  exit /b 1
)

if not exist "%JDK8%\bin\java.exe" (
  echo.
  echo   JDK 8 not found at %JDK8%
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

if not exist "%BUN%" (
  echo.
  echo   Bun not found at %BUN%
  echo   The server cannot start without it.
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
rem java.exe, not javaw: if the client throws on startup the stack trace lands in
rem client.log instead of disappearing silently.
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
pushd "%ROOT%Client-Java"
start "Bwana client" /MIN "%JDK8%\bin\java.exe" %JVM% -jar "%JAR%" %ARGS%
popd
exit /b 0
