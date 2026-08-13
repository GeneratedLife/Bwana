@echo off
setlocal
title Bwana - build

rem ---------------------------------------------------------------------------
rem  Rebuilds the client jar. Run this after changing any Java source.
rem
rem  JAVA_HOME is not set here on purpose: ~/.gradle/gradle.properties pins
rem  org.gradle.java.home to JDK 17 for the Gradle daemon and lists both JDKs for
rem  toolchain resolution, so the build works however it is launched.
rem ---------------------------------------------------------------------------

cd /d "%~dp0Client-Java"
call gradlew.bat build --console=plain
if errorlevel 1 (
  echo.
  echo   Build FAILED - see the errors above.
  echo.
  pause
  exit /b 1
)

echo.
echo   Built: %~dp0Client-Java\client-225\build\libs\rs2client.jar
echo.
pause
exit /b 0
