@echo off
REM Combined Load Test & Virtual Threads Monitor Runner (Windows)
REM This script runs k6 load test and virtual threads monitor simultaneously

setlocal enabledelayedexpansion

REM Configuration
set SCENARIO=%1
if "%SCENARIO%"=="" set SCENARIO=constant_load

set BASE_URL=http://localhost:8081
set AUTH_TOKEN=eyJhbGciOiJIUzI1NiJ9.eyJlbWFpbCI6ImNhY2hlX3Rlc3RfdXNlcl8xNzY2NDEyMDM3QGV4YW1wbGUuY29tIiwic3ViIjoiY2FjaGVfdGVzdF91c2VyXzE3NjY0MTIwMzdAZXhhbXBsZS5jb20iLCJpYXQiOjE3NjY0MTIwMzksImV4cCI6MTc2NjQ1NTIzOX0.-yXGScOUtPCmKj_zrekKUNHxt3U-JG-y1EmmonFd2_w
set MONITOR_INTERVAL=1

REM Timestamp
for /f "tokens=2-4 delims=/ " %%a in ('date /t') do (set mydate=%%c%%a%%b)
for /f "tokens=1-2 delims=/: " %%a in ('time /t') do (set mytime=%%a%%b)
set TIMESTAMP=%mydate%_%mytime%
set RESULTS_DIR=k6-results\%TIMESTAMP%

echo ========================================
echo   K6 Load Test + Virtual Threads Monitor
echo ========================================
echo Configuration:
echo   Scenario:        %SCENARIO%
echo   Base URL:        %BASE_URL%
echo   Monitor Interval: %MONITOR_INTERVAL%s
echo   Results Dir:     %RESULTS_DIR%
echo ========================================
echo.

REM Create results directory
if not exist "k6-results" mkdir k6-results
mkdir "%RESULTS_DIR%"

REM Check if k6 is installed
where k6 >nul 2>nul
if %ERRORLEVEL% neq 0 (
    echo Error: k6 is not installed
    echo Install k6: https://k6.io/docs/getting-started/installation/
    exit /b 1
)

REM Check if server is running
echo Checking if server is running...
curl -s "%BASE_URL%/api/v1/monitor/threads" >nul 2>nul
if %ERRORLEVEL% neq 0 (
    echo Error: Server is not running at %BASE_URL%
    echo Please start the application first
    exit /b 1
)

echo Server is running!
echo.

REM Start virtual threads monitor in background
echo Starting Virtual Threads Monitor...
start /B "VirtualThreadsMonitor" cmd /C "k6\monitor-virtual-threads.bat"
timeout /t 2 /nobreak >nul
echo Monitor started
echo.

REM Run k6 load test
echo Starting K6 Load Test (%SCENARIO%)...
echo ========================================
echo.

k6 run --out json="%RESULTS_DIR%\k6-results.json" --summary-export="%RESULTS_DIR%\k6-summary.json" --scenarios %SCENARIO% k6\topup-load-test.js

echo.
echo ========================================
echo   Test Complete!
echo ========================================
echo Results saved to: %RESULTS_DIR%
echo.

pause
