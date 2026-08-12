@echo off
setlocal
title Bwana - stop

rem ---------------------------------------------------------------------------
rem  Stops the client and the server.
rem
rem  The client is closed rather than killed: closing the window lets the JVM run
rem  its shutdown hooks, which is what flushes the XP session and any unsaved
rem  notes to disk. Killing it loses them.
rem ---------------------------------------------------------------------------

echo Closing the client...
taskkill /IM javaw.exe /FI "WINDOWTITLE eq Jagex" >nul 2>&1
taskkill /IM java.exe /FI "WINDOWTITLE eq Jagex" >nul 2>&1
timeout /t 2 >nul

echo Stopping the server...
taskkill /IM bun.exe /F >nul 2>&1

echo Done.
timeout /t 2 >nul
exit /b 0
