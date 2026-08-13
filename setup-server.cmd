@echo off
setlocal enabledelayedexpansion
title Bwana - server setup

rem ---------------------------------------------------------------------------
rem  Fetches and prepares a Lost City server for one revision.
rem
rem    setup-server.cmd            225, beside this checkout
rem    setup-server.cmd 274        274, beside this checkout
rem    setup-server.cmd 274 D:\lc  274, at a path you name
rem
rem  The server is a separate project and is deliberately not vendored here; this
rem  only clones it and wires it to the ports the matching client asks for.
rem
rem  Engine and content are versioned in branches and have to match each other and
rem  the client, so both are cloned from the branch named by the revision. They
rem  must sit beside each other, because the engine's neptune.toml reads its
rem  scripts from ../content/scripts/.
rem
rem  Layout produced:
rem
rem    <parent>\Server-<rev>\engine    Engine-TS, branch <rev>
rem    <parent>\Server-<rev>\content   Content,   branch <rev>
rem
rem  Beside this checkout rather than inside it, so a few hundred MB of server
rem  tree never shows up in git status. Revisions get their own directory and
rem  their own ports so two worlds can run at once.
rem
rem  225 is the exception: it predates this argument and stays at <parent>\Server
rem  when that already exists, so an install made before now keeps working and
rem  play.cmd keeps finding it.
rem ---------------------------------------------------------------------------

set "ROOT=%~dp0"

rem --- which revision ----------------------------------------------------------

rem  The revision, its port offset and where its server lives all come from
rem  revision.cmd, which the launchers read too. Keeping a second copy of the
rem  offset here was the obvious thing and the wrong one: an offset that agrees
rem  in the launcher but not in the .env fails as "the client cannot connect",
rem  which names neither file.
call "%ROOT%revision.cmd" %1
if errorlevel 1 (
  pause
  exit /b 1
)

rem --- where it goes -----------------------------------------------------------

rem  revision.cmd only reports a server it can already see. This one creates it,
rem  so it needs the intended path whether or not anything is there yet.
if not defined SERVER for %%I in ("%ROOT%..\Server-%REV%") do set "SERVER=%%~fI"
if defined BWANA_SERVER set "SERVER=%BWANA_SERVER%"
if not "%~2"=="" set "SERVER=%~2"
for %%I in ("%SERVER%") do set "SERVER=%%~fI"

set "NODEID=10"

echo.
echo   Revision    : %REV%
echo   Server root : %SERVER%
echo   Ports       : web %WEBPORT%, game %GAMEPORT%
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
  echo   engine  : cloning Engine-TS branch %REV%
  "%GIT%" clone --depth 1 -b %REV% --single-branch https://github.com/LostCityRS/Engine-TS "%SERVER%\engine"
  if errorlevel 1 goto clonefailed
)

if exist "%SERVER%\content\.git" (
  call :checkbranch "%SERVER%\content" "content" Content
  if errorlevel 1 exit /b 1
) else (
  echo   content : cloning Content branch %REV%
  "%GIT%" clone --depth 1 -b %REV% --single-branch https://github.com/LostCityRS/Content "%SERVER%\content"
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
  >  "%SERVER%\engine\.env" echo # Written by setup-server.cmd for revision %REV%.
  >> "%SERVER%\engine\.env" echo # The client is told "%NODEID% %OFFSET% highmem members" and turns that
  >> "%SERVER%\engine\.env" echo # offset into http 80+%OFFSET% and game 43594+%OFFSET%. Change both together.
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
rem  Ask the launchers' own lookup whether it can see what was just built, rather
rem  than restating where it searches and letting the two drift.
set "MADE=%SERVER%"
call "%ROOT%revision.cmd" %REV%
set "FOUNDBYPLAY="
if /i "%SERVER%"=="%MADE%" set "FOUNDBYPLAY=1"
set "SERVER=%MADE%"

if not defined FOUNDBYPLAY (
  echo   play.cmd will not find it there, so name it:
  echo.
  echo     set BWANA_SERVER=%SERVER%
  echo.
)
echo   Then run:  play.cmd %REV%
echo.
echo   First start takes about a minute while the world loads; the client
echo   waits for it.
echo.
pause
exit /b 0

:checkbranch
rem  %1 = repo dir, %2 = padded label for the report, %3 = repo name on GitHub.
rem  Returns 1 if the checkout is not on the revision being set up.
set "BR="
rem  Plain `git`, not "%GIT%": a for /f command that opens with a quoted path
rem  runs into cmd's own quote stripping. It was found on PATH above, so the
rem  bare name resolves to the same executable.
for /f "delims=" %%B in ('git -C "%~1" rev-parse --abbrev-ref HEAD 2^>nul') do set "BR=%%B"
if /i "%BR%"=="%REV%" (
  echo   %~2 : on branch %REV% already
  exit /b 0
)
echo.
echo   %~3 at %~1
echo   is on branch "%BR%", but this is a %REV% server, and the engine, the
echo   content and the client all have to be the same revision.
echo.
echo   Branch 274 in particular runs on Node and imports node:sqlite, so under
echo   Bun it dies with "No such built-in module: node:sqlite" -- a message that
echo   names neither the branch nor the revision.
echo.
echo   Switch it over:
echo.
echo     cd /d "%~1"
echo     git remote set-branches --add origin %REV%
echo     git fetch origin %REV%
echo     git checkout %REV%
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
echo   if the branch is missing, check what %REV% branches exist at
echo   https://github.com/LostCityRS/Engine-TS/branches
echo.
pause
exit /b 1
