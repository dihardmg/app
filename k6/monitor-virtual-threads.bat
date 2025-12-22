@echo off
REM Virtual Threads Monitoring Script for k6 Load Testing (Windows)
REM This script monitors virtual threads while k6 load test is running

setlocal enabledelayedexpansion

REM Configuration
set BASE_URL=http://localhost:8081
set MONITOR_INTERVAL=2

REM Timestamp
for /f "tokens=2-4 delims=/ " %%a in ('date /t') do (set mydate=%%c%%a%%b)
for /f "tokens=1-2 delims=/: " %%a in ('time /t') do (set mytime=%%a%%b)
set OUTPUT_FILE=virtual-threads-monitor-%mydate%_%mytime%.csv
set LOG_FILE=virtual-threads-monitor-%mydate%_%mytime%.log

echo ========================================
echo Virtual Threads Monitor
echo ========================================
echo Monitoring URL: %BASE_URL%
echo Monitor Interval: %MONITOR_INTERVAL%s
echo Output File: %OUTPUT_FILE%
echo Log File: %LOG_FILE%
echo ========================================
echo.

REM Create CSV header
echo timestamp,total_threads,virtual_threads,platform_threads,peak_threads,daemon_threads,current_is_virtual,current_thread_name,current_thread_id > "%OUTPUT_FILE%"

:monitor_loop
REM Fetch thread info
for /f "delims=" %%i in ('curl -s "%BASE_URL%/api/v1/monitor/threads"') do set JSON=%%i

REM Parse using PowerShell (more reliable on Windows)
for /f "tokens=* delims=" %%a in ('powershell -Command "$json = '%JSON%'; $timestamp = Get-Date -Format 'yyyy-MM-dd_HH:mm:ss.fff'; $totalThreads = ($json | ConvertFrom-Json).totalThreadCount; $virtualThreads = ($json | ConvertFrom-Json).virtualThreadCount; $platformThreads = ($json | ConvertFrom-Json).platformThreadCount; $currentIsVirtual = ($json | ConvertFrom-Json).currentThread.isVirtual; Write-Output \"$timestamp,$totalThreads,$virtualThreads,$platformThreads,$currentIsVirtual\""') do set LINE=%%a

REM Display
echo !LINE!

REM Write to CSV
echo !LINE! >> "%OUTPUT_FILE%"

REM Wait
timeout /t %MONITOR_INTERVAL% /nobreak >nul

goto monitor_loop
