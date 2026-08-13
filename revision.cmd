@echo off
rem ---------------------------------------------------------------------------
rem  Everything that differs between revisions, in one place.
rem
rem  CALLED, never run on its own, and deliberately without setlocal so the
rem  variables it sets survive back into the caller:
rem
rem    call "%~dp0revision.cmd" 274
rem    if errorlevel 1 exit /b 1
rem
rem  Sets, for the revision given (225 when none is):
rem
rem    REV        the revision, and the git branch of engine and content
rem    OFFSET     port offset passed to the client
rem    WEBPORT    80 + OFFSET
rem    GAMEPORT   43594 + OFFSET
rem    CLIENTARGS the full argument line that revision's client expects
rem    JAR        the built client jar
rem    SERVER     the server checkout, if one is where it is expected
rem
rem  It exists because these facts were about to be written out three times --
rem  in play.cmd, run-client.cmd and setup-server.cmd -- and a port offset that
rem  agrees in two of three places fails as "the client cannot connect", which
rem  says nothing about which file is wrong.
rem
rem  ADDING A REVISION: add its line to each table below. All of them: a missing
rem  entry stops rather than falling back, because every default here would be
rem  another revision's value and would look like a bug in the client.
rem ---------------------------------------------------------------------------

set "ROOT=%~dp0"

set "REV=%~1"
if not defined REV set "REV=225"

echo %REV%| findstr /r "^[0-9][0-9]*$" >nul
if errorlevel 1 (
  echo.
  echo   "%REV%" is not a revision. Pass a number, or nothing for 225.
  echo.
  exit /b 1
)

rem --- ports -------------------------------------------------------------------

rem  One offset per revision so two worlds can be up at once. The client turns the
rem  offset into http 80+offset and game 43594+offset; the engine is told the same
rem  numbers by setup-server.cmd, in that revision's .env.
set "OFFSET="
if "%REV%"=="225" set "OFFSET=2000"
if "%REV%"=="274" set "OFFSET=2010"

if not defined OFFSET (
  echo.
  echo   No port offset is set for revision %REV%. Add one to revision.cmd,
  echo   picking a number no other revision uses.
  echo.
  exit /b 1
)

set /a WEBPORT=80+%OFFSET%
set /a GAMEPORT=43594+%OFFSET%

rem --- client arguments ---------------------------------------------------------

rem  Not the same shape across revisions, and getting it wrong is quiet: the
rem  client prints its usage line and exits, so the window closes with nothing in
rem  it. 225 takes four arguments; 274 takes five, the last being signlink's
rem  storeid, which it clamps to 32-34 and defaults to 32.
set "CLIENTARGS="
if "%REV%"=="225" set "CLIENTARGS=10 %OFFSET% highmem members"
if "%REV%"=="274" set "CLIENTARGS=10 %OFFSET% highmem members 32"

if not defined CLIENTARGS (
  echo.
  echo   No client arguments are set for revision %REV%. Add them to
  echo   revision.cmd, matching that client's main^(^) usage line.
  echo.
  exit /b 1
)

rem --- the jar ------------------------------------------------------------------

rem  The two layouts build-home.cmd handles: a plain clone is itself the gradle
rem  project, while the work PC keeps the client in a Client-Java folder with the
rem  scripts a level above it.
set "CLIENT=%ROOT%"
if exist "%ROOT%Client-Java\build.gradle" set "CLIENT=%ROOT%Client-Java\"

set "JAR=%CLIENT%client-%REV%\build\libs\rs2client.jar"

rem  Only 225 ever had a jar outside a module, from before the core/client split.
if "%REV%"=="225" if not exist "%JAR%" if exist "%CLIENT%build\libs\rs2client.jar" set "JAR=%CLIENT%build\libs\rs2client.jar"

rem --- the server ---------------------------------------------------------------

rem  BWANA_SERVER first, then this revision's own directory, then -- for 225 only
rem  -- the unsuffixed one that existed before setup-server.cmd took a revision.
set "SERVER="
if defined BWANA_SERVER (
  if exist "%BWANA_SERVER%\engine\src\app.ts" set "SERVER=%BWANA_SERVER%"
)
if not defined SERVER if exist "%ROOT%..\Server-%REV%\engine\src\app.ts" set "SERVER=%ROOT%..\Server-%REV%"
if not defined SERVER if exist "%ROOT%Server-%REV%\engine\src\app.ts"    set "SERVER=%ROOT%Server-%REV%"
if "%REV%"=="225" (
  if not defined SERVER if exist "%ROOT%..\Server\engine\src\app.ts" set "SERVER=%ROOT%..\Server"
  if not defined SERVER if exist "%ROOT%Server\engine\src\app.ts"    set "SERVER=%ROOT%Server"
)

exit /b 0
