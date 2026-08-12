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
if defined BWANA_JDK8 (
  set "JDK8=%BWANA_JDK8%"
) else (
  set "JDK8=%LOCALAPPDATA%\jdks\jdk8u502-b07"
)
rem  JAVA_HOME often carries a trailing backslash, which would print and pass
rem  through as ...zulu-21\\bin\java.exe. Windows accepts that, but it reads like
rem  a bug in the output, so trim it.
if not exist "%JDK8%\bin\java.exe" if exist "%JAVA_HOME%\bin\java.exe" (
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

if not exist "%JDK8%\bin\java.exe" (
  echo.
  echo   No java.exe found. Looked at:
  echo     %JDK8%
  echo.
  echo   Point at a JDK 8:  set BWANA_JDK8=C:\path\to\jdk8
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
