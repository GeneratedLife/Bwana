@echo off
setlocal enabledelayedexpansion
title Bwana - build (home)

rem ---------------------------------------------------------------------------
rem  Rebuilds the client jar on a machine that has no ~/.gradle/gradle.properties.
rem
rem  build.cmd sets nothing on purpose, because on the work PC that user-home
rem  file pins org.gradle.java.home to JDK 17 and lists both JDKs for toolchain
rem  resolution. It is not in the repo, so a fresh clone elsewhere gets neither
rem  setting and the build stops at "No matching toolchains found for Java 8".
rem
rem  This script works the paths out itself and passes them on the command line,
rem  so nothing outside the repo has to be set up first. It needs two JDKs,
rem  which may well be the same one:
rem
rem    - a JDK 8, because build.gradle asks the toolchain for language level 8
rem    - a JDK to run Gradle itself; 11+ is preferred, 8 works
rem
rem  A JRE is not enough for either: the search only accepts a directory that
rem  has bin\javac.exe, which is what keeps it off the 32-bit system JRE that
rem  sits on PATH.
rem
rem  If the search misses, point it straight at them and run again:
rem
rem    set BWANA_JDK8=C:\path\to\jdk8
rem    set BWANA_JDK_DAEMON=C:\path\to\jdk
rem ---------------------------------------------------------------------------

set "PROBE=%TEMP%\bwana-jdk-probe.txt"

rem --- where is the gradle project --------------------------------------------

rem  Two layouts are in play. The repo is the gradle project, so a plain clone
rem  puts gradlew.bat next to this script. The work PC keeps the client checked
rem  out as a Client-Java folder with the scripts a level above it, which is the
rem  layout build.cmd assumes. Accept whichever is actually on disk.

set "PROJECT=%~dp0"
if exist "%PROJECT%gradlew.bat" goto haveproject

if exist "%PROJECT%Client-Java\gradlew.bat" (
  set "PROJECT=%PROJECT%Client-Java\"
  goto haveproject
)

echo.
echo   No gradlew.bat found, looked in:
echo     %~dp0
echo     %~dp0Client-Java\
echo.
echo   Run this from the root of the Bwana checkout.
echo.
pause
exit /b 1

:haveproject

rem --- the wrapper jar ---------------------------------------------------------

rem  gradlew.bat is only a launcher. It sets
rem    CLASSPATH=%APP_HOME%\gradle\wrapper\gradle-wrapper.jar
rem  where APP_HOME is its own directory, then runs GradleWrapperMain out of that
rem  jar. .gitignore's *.jar rule kept the file out of the repo, so a clone hits
rem  ClassNotFoundException before Gradle ever starts. Check for it here and say
rem  which path is actually wanted, since the jar has to sit in one exact place.

set "WRAPJAR=%PROJECT%gradle\wrapper\gradle-wrapper.jar"

if not exist "%WRAPJAR%" (
  echo.
  echo   The Gradle wrapper jar is missing. It goes exactly here:
  echo.
  echo     %WRAPJAR%
  echo.
  echo   Note gradlew.bat reads that path relative to itself, so the jar has to
  echo   be under the folder holding gradlew.bat, in gradle\wrapper\.
  echo.
  echo   Similar files found under the checkout:
  set "FOUND="
  for /r "%PROJECT%" %%J in (*wrapper*.jar*) do (
    echo     %%~fJ  ^(%%~zJ bytes^)
    set "FOUND=1"
  )
  if not defined FOUND echo     none
  echo.
  pause
  exit /b 1
)

rem  A truncated download or a saved error page will be far smaller than the real
rem  jar, which is about 43 KB, and produces the same ClassNotFoundException.
for %%J in ("%WRAPJAR%") do set "JARSIZE=%%~zJ"
if %JARSIZE% lss 10000 (
  echo.
  echo   The wrapper jar looks wrong - %JARSIZE% bytes, expected about 43000:
  echo.
  echo     %WRAPJAR%
  echo.
  echo   That is usually a truncated download, or an HTML error page saved
  echo   under the .jar name. Replace it and run this again.
  echo.
  pause
  exit /b 1
)

rem --- which JDKs --------------------------------------------------------------

set "JDK8="
set "DAEMON="
set "MODERN="
set "MODERNVER=0"
set "TOONEW="

rem  The newest JDK on the machine is the wrong choice for running Gradle: each
rem  Gradle release only knows class file versions up to the Java it shipped
rem  against. gradle-wrapper.properties pins 8.11.1, which is from November 2024
rem  and tops out at Java 23; handing it Java 24 fails while parsing build.gradle
rem  with "Unsupported class file major version 68", not with anything that names
rem  the JDK. Cap the search, and raise this if the wrapper is ever bumped.
set "MAXDAEMON=23"

if defined BWANA_JDK8 (
  if exist "%BWANA_JDK8%\bin\javac.exe" (
    set "JDK8=%BWANA_JDK8%"
  ) else (
    echo.
    echo   BWANA_JDK8 is set to "%BWANA_JDK8%"
    echo   but there is no bin\javac.exe there.
    echo.
    pause
    exit /b 1
  )
)

if defined BWANA_JDK_DAEMON (
  if exist "%BWANA_JDK_DAEMON%\bin\javac.exe" (
    set "DAEMON=%BWANA_JDK_DAEMON%"
  ) else (
    echo.
    echo   BWANA_JDK_DAEMON is set to "%BWANA_JDK_DAEMON%"
    echo   but there is no bin\javac.exe there.
    echo.
    pause
    exit /b 1
  )
)

rem  jdks\ first: that is where play.cmd expects the portable JDK 8 to live, so
rem  a machine set up for playing already has the one the toolchain wants.
if not defined JDK8 (
  for /d %%D in ("%LOCALAPPDATA%\jdks\*")             do call :consider "%%~fD"
  for /d %%D in ("%USERPROFILE%\.jdks\*")             do call :consider "%%~fD"
  for /d %%D in ("%ProgramFiles%\Java\*")             do call :consider "%%~fD"
  for /d %%D in ("%ProgramFiles%\Eclipse Adoptium\*") do call :consider "%%~fD"
  for /d %%D in ("%ProgramFiles%\Microsoft\*")        do call :consider "%%~fD"
  for /d %%D in ("%ProgramFiles%\Amazon Corretto\*")  do call :consider "%%~fD"
  for /d %%D in ("%ProgramFiles%\Zulu\*")             do call :consider "%%~fD"
  if exist "%JAVA_HOME%\bin\javac.exe" call :consider "%JAVA_HOME%"
)

if exist "%PROBE%" del "%PROBE%" >nul 2>&1

if not defined JDK8 (
  echo.
  echo   No JDK 8 found. The toolchain in build.gradle asks for language
  echo   level 8, so one has to be on disk. Looked under:
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

rem  Gradle 8.11 runs on 8 or newer, so the JDK 8 is a usable last resort for the
rem  daemon. A JDK in range is preferred only because it is faster.
if not defined DAEMON (
  if defined MODERN (
    set "DAEMON=!MODERN!"
  ) else (
    set "DAEMON=!JDK8!"
  )
)

if defined TOONEW (
  echo   Skipped         : !TOONEW!
  echo                     too new to run Gradle 8.11.1, which supports Java %MAXDAEMON%
)

rem  Hand Gradle every JDK found, not just the toolchain one, so it can still
rem  resolve if build.gradle ever asks for a second language level.
set "PATHS=%JDK8%"
if defined MODERN if /i not "%MODERN%"=="%JDK8%" set "PATHS=%JDK8%,%MODERN%"

echo   Toolchain JDK 8 : %JDK8%
echo   Gradle runs on  : %DAEMON%
echo.

rem --- build -------------------------------------------------------------------

pushd "%PROJECT%"
call gradlew.bat build --console=plain "-Dorg.gradle.java.home=%DAEMON%" "-Porg.gradle.java.installations.paths=%PATHS%"
set "RC=%ERRORLEVEL%"
popd

if not "%RC%"=="0" (
  echo.
  echo   Build FAILED - see the errors above.
  echo.
  pause
  exit /b 1
)

echo.
echo   Built: %PROJECT%build\libs\rs2client.jar
echo.
pause
exit /b 0

rem --- subroutines -------------------------------------------------------------

:consider
rem  %1 = a candidate JDK directory. Records it if it is the first JDK 8 seen, or
rem  the newest JDK between 11 and MAXDAEMON. Anything newer than MAXDAEMON is
rem  remembered only so the run can say why it was passed over.
call :probe "%~1"
if not defined VER exit /b
if "%VER%"=="8" (
  if not defined JDK8 set "JDK8=%~1"
  exit /b
)
if %VER% geq 11 (
  if %VER% leq %MAXDAEMON% (
    if %VER% gtr %MODERNVER% (
      set "MODERN=%~1"
      set "MODERNVER=%VER%"
    )
  ) else (
    set "TOONEW=%~1 - Java %VER%"
  )
)
exit /b

:probe
rem  %1 = a candidate JDK directory. Sets VER to its major version, or leaves it
rem  empty if the directory is not a JDK. The version goes through a file rather
rem  than a pipe because java prints -version to stderr and the quoting needed to
rem  capture that inline breaks on paths containing spaces.
set "VER="
set "RAW="
if not exist "%~1\bin\javac.exe" exit /b
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
