@echo off
setlocal enabledelayedexpansion
title Bwana - server setup

rem ---------------------------------------------------------------------------
rem  Fetches and prepares the Lost City server that play.cmd starts. The server
rem  is a separate project and is deliberately not vendored here; this only
rem  clones it and wires it to the ports the client already asks for.
rem
rem  Engine and content are versioned in branches and have to match: the client
rem  in this repo is rev 225, so both are cloned from their 225 branches. They
rem  must sit beside each other, because the engine's neptune.toml reads its
rem  scripts from ../content/scripts/.
rem
rem  Layout produced, which is where play.cmd looks:
rem
rem    <parent>\Server\engine     Engine-TS,  branch 225
rem    <parent>\Server\content    Content,    branch 225
rem
rem  By default <parent>\Server is a sibling of this checkout rather than inside
rem  it, so a few hundred MB of server tree never shows up in git status. Pass a
rem  path to put it elsewhere:
rem
rem    setup-server.cmd D:\lostcity\Server
rem ---------------------------------------------------------------------------

set "ROOT=%~dp0"

rem  The two places play.cmd looks, resolved to real paths so they can be compared
rem  against wherever this ends up putting things.
for %%I in ("%ROOT%..\Server") do set "BESIDE=%%~fI"
for %%I in ("%ROOT%Server")    do set "INSIDE=%%~fI"

set "SERVER=%BESIDE%"
if defined BWANA_SERVER set "SERVER=%BWANA_SERVER%"
if not "%~1"=="" set "SERVER=%~1"
for %%I in ("%SERVER%") do set "SERVER=%%~fI"

rem  Ports have to agree with play.cmd or the client will never find the server.
rem  play.cmd passes the client "10 2000 highmem members": node id 10 and a port
rem  offset of 2000, and the client turns that offset into http 80+2000 and game
rem  43594+2000. The engine defaults to 80 and 43594, so it needs telling.
set "NODEID=10"
set "WEBPORT=2080"
set "GAMEPORT=45594"

echo.
echo   Server root : %SERVER%
echo.

rem --- prerequisites -----------------------------------------------------------

for %%G in (git.exe) do set "GIT=%%~$PATH:G"
if not defined GIT (
  echo   git is not on PATH. Install Git for Windows from https://git-scm.com
  echo.
  pause
  exit /b 1
)

set "BUN=%USERPROFILE%\.bun\bin\bun.exe"
if not exist "%BUN%" for %%B in (bun.exe) do set "BUN=%%~$PATH:B"
if not exist "%BUN%" (
  echo   Bun is not installed. The engine runs on it. Install with:
  echo.
  echo     powershell -c "irm bun.sh/install.ps1 ^| iex"
  echo.
  echo   then run this script again.
  echo.
  pause
  exit /b 1
)

rem  The engine shells out to java to pack content. Not fatal here -- the clone
rem  and install still work -- but the server will fail later without it, and the
rem  error it gives ^("'java' is not recognized"^) does not say why.
set "JAVA="
for %%J in (java.exe) do set "JAVA=%%~$PATH:J"
if not defined JAVA (
  echo   Warning: no java on PATH. The engine needs Java 17 or newer to pack
  echo   content, and will fail with "'java' is not recognized" when it tries.
  echo   Add a JDK 17+ bin folder to PATH before playing.
  echo.
)

rem --- engine and content ------------------------------------------------------

rem  An existing checkout is not necessarily the right one, and the wrong branch
rem  fails in a way that names neither the branch nor the revision: 274 imports
rem  node:sqlite and runs on Node, so under Bun it dies with "No such built-in
rem  module: node:sqlite", while 225 uses bun:sqlite. Check rather than assume.

if exist "%SERVER%\engine\.git" (
  call :checkbranch "%SERVER%\engine" "engine " Engine-TS
  if errorlevel 1 exit /b 1
) else (
  echo   engine  : cloning Engine-TS branch 225
  "%GIT%" clone --depth 1 -b 225 --single-branch https://github.com/LostCityRS/Engine-TS "%SERVER%\engine"
  if errorlevel 1 goto clonefailed
)

if exist "%SERVER%\content\.git" (
  call :checkbranch "%SERVER%\content" "content" Content
  if errorlevel 1 exit /b 1
) else (
  echo   content : cloning Content branch 225
  "%GIT%" clone --depth 1 -b 225 --single-branch https://github.com/LostCityRS/Content "%SERVER%\content"
  if errorlevel 1 goto clonefailed
)

rem --- ports -------------------------------------------------------------------

rem  Never overwrite an .env that already exists: it is the one file here anyone
rem  is likely to have edited by hand.
if exist "%SERVER%\engine\.env" (
  echo   .env    : already present, leaving it alone
  echo             check WEB_PORT=%WEBPORT% and NODE_PORT=%GAMEPORT% if the
  echo             client cannot connect
) else (
  echo   .env    : writing, web %WEBPORT% / game %GAMEPORT%
  >  "%SERVER%\engine\.env" echo # Written by setup-server.cmd to match play.cmd's client arguments.
  >> "%SERVER%\engine\.env" echo # The client is told "%NODEID% 2000 highmem members" and turns that
  >> "%SERVER%\engine\.env" echo # offset into http 80+2000 and game 43594+2000. Change both together.
  >> "%SERVER%\engine\.env" echo NODE_ID=%NODEID%
  >> "%SERVER%\engine\.env" echo WEB_PORT=%WEBPORT%
  >> "%SERVER%\engine\.env" echo NODE_PORT=%GAMEPORT%
)

rem --- dependencies ------------------------------------------------------------

rem  play.cmd runs `bun run src/app.ts` directly rather than `bun start`, and it
rem  is `bun start` that would have done `bun install` first. So the install has
rem  to happen here or the server dies on its first missing module.
echo.
echo   Installing engine dependencies. This takes a few minutes.
echo.
pushd "%SERVER%\engine"
"%BUN%" install
set "RC=%ERRORLEVEL%"
popd

if not "%RC%"=="0" (
  echo.
  echo   bun install FAILED - see above.
  echo.
  pause
  exit /b 1
)

echo.
echo   Done. The server is at:
echo     %SERVER%
echo.
set "FOUNDBYPLAY="
if /i "%SERVER%"=="%BESIDE%" set "FOUNDBYPLAY=1"
if /i "%SERVER%"=="%INSIDE%" set "FOUNDBYPLAY=1"
if not defined FOUNDBYPLAY (
  echo   That is not one of the two places play.cmd looks, so name it:
  echo.
  echo     set BWANA_SERVER=%SERVER%
  echo.
)
echo   Then run play.cmd. First start takes about a minute while the world
echo   loads; the client waits for it.
echo.
pause
exit /b 0

:checkbranch
rem  %1 = repo dir, %2 = padded label for the report, %3 = repo name on GitHub.
rem  Returns 1 if the checkout is not on 225.
set "BR="
rem  Plain `git`, not "%GIT%": a for /f command that opens with a quoted path
rem  runs into cmd's own quote stripping. It was found on PATH above, so the
rem  bare name resolves to the same executable.
for /f "delims=" %%B in ('git -C "%~1" rev-parse --abbrev-ref HEAD 2^>nul') do set "BR=%%B"
if /i "%BR%"=="225" (
  echo   %~2 : on branch 225 already
  exit /b 0
)
echo.
echo   %~3 at %~1
echo   is on branch "%BR%", but this client is rev 225 and the engine, the
echo   content and the client all have to be the same revision.
echo.
echo   Branch 274 in particular runs on Node and imports node:sqlite, so under
echo   Bun it dies with "No such built-in module: node:sqlite".
echo.
echo   Switch it over:
echo.
echo     cd /d "%~1"
echo     git remote set-branches --add origin 225
echo     git fetch origin 225
echo     git checkout 225
echo.
if /i "%~3"=="Engine-TS" (
  echo   Then clear out dependencies installed for the other branch, because
  echo   they came from a different lockfile:
  echo.
  echo     rmdir /s /q "%~1\node_modules"
  echo.
)
echo   Or delete %SERVER% entirely and run this again for a clean pair. That
echo   discards any world and player data under it.
echo.
pause
exit /b 1

:clonefailed
echo.
echo   Clone FAILED - see above. If it is a network or proxy problem, retry;
echo   if the branch is missing, check what 225 branches exist at
echo   https://github.com/LostCityRS/Engine-TS/branches
echo.
pause
exit /b 1
