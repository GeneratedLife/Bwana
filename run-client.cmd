@echo off
setlocal
title Bwana - run client (foreground)

rem ---------------------------------------------------------------------------
rem  Runs the already-built client in this window, to find out why it will not
rem  start. play.cmd launches it with `start /MIN`, so a startup exception is
rem  thrown inside a minimised window that closes with the process: the comment
rem  above that line promises client.log, but the command has no redirection and
rem  nothing ever writes one. Here the client owns this console and the window
rem  stays open afterwards, so the trace can be read.
rem
rem  This does not start the server. Either leave play.cmd's server window up, or
rem  expect the client to sit on "Error loading - will retry in NN secs", which
rem  means no server is listening rather than anything being broken.
rem
rem  For a file to attach somewhere, redirect the whole script:
rem
rem    run-client.cmd > client.log 2>&1
rem ---------------------------------------------------------------------------

set "ROOT=%~dp0"

set "CLIENT=%ROOT%"
if exist "%ROOT%Client-Java\build.gradle" set "CLIENT=%ROOT%Client-Java\"
set "JAR=%CLIENT%build\libs\rs2client.jar"

rem  Same arguments and JVM flags play.cmd uses, so this reproduces that launch
rem  rather than a different one.
set "ARGS=10 2000 highmem members"
set "JVM=-Xms1536m -Xmx1536m -XX:+UseG1GC -XX:MaxGCPauseMillis=40"

rem  Same JDK 8 resolution order as play.cmd and build-home.cmd, falling back to
rem  JAVA_HOME. The system JRE on PATH is deliberately not a candidate: it is
rem  32-bit, and the client will not get a 1536m heap out of it.
set "PROBE=%TEMP%\bwana-jdk-probe.txt"

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

rem  Unlike play.cmd this then settles for JAVA_HOME whatever version it is. The
rem  point here is to see the client's output, and it starts on a newer JDK just
rem  as well -- so a machine with no JDK 8 at all can still get a trace out.
rem  JAVA_HOME often carries a trailing backslash, which would print and pass
rem  through as ...zulu-21\\bin\java.exe. Windows accepts that, but it reads like
rem  a bug in the output, so trim it.
if not defined JDK8 if exist "%JAVA_HOME%\bin\java.exe" (
  set "JDK8=%JAVA_HOME%"
  if "%JAVA_HOME:~-1%"=="\" set "JDK8=%JAVA_HOME:~0,-1%"
)

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
  echo   No JDK found. Looked under:
  echo.
  echo     %LOCALAPPDATA%\jdks\
  echo     %USERPROFILE%\.jdks\
  echo     %ProgramFiles%\Java\ ^(and Adoptium, Microsoft, Corretto, Zulu^)
  echo.
  echo   and at JAVA_HOME, which is %JAVA_HOME%
  echo.
  echo   Point at one:  set BWANA_JDK8=C:\path\to\jdk8
  echo.
  pause
  exit /b 1
)

echo   java : %JDK8%\bin\java.exe
echo   jar  : %JAR%
echo   args : %ARGS%
echo.
echo   ---------------- client output below ----------------
echo.

pushd "%CLIENT%"
"%JDK8%\bin\java.exe" %JVM% -jar "%JAR%" %ARGS%
set "RC=%ERRORLEVEL%"
popd

echo.
echo   ---------------- client exited, code %RC% ----------------
echo.
pause
exit /b %RC%

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
