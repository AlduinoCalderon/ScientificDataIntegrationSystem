@echo off
REM Scientific Data Integration System - Batch Conversion Script
REM Usage: batch-convert.bat <input_directory> <output_directory>

setlocal enabledelayedexpansion

if "%~2"=="" (
    echo Usage: %0 ^<input_directory^> ^<output_directory^>
    echo Example: %0 "data\input" "data\output"
    exit /b 1
)

set "INPUT_DIR=%~1"
set "OUTPUT_DIR=%~2"
set "JAR_FILE=target\dataintegration-1.0-SNAPSHOT.jar"

echo [%date% %time%] Starting batch JSON to CSV conversion...
echo [%date% %time%] Input Directory: %INPUT_DIR%
echo [%date% %time%] Output Directory: %OUTPUT_DIR%

REM Check if JAR file exists
if not exist "%JAR_FILE%" (
    echo [%date% %time%] ERROR: JAR file not found: %JAR_FILE%
    exit /b 1
)

REM Create output directory if it doesn't exist
if not exist "%OUTPUT_DIR%" (
    mkdir "%OUTPUT_DIR%"
    echo [%date% %time%] Created output directory: %OUTPUT_DIR%
)

REM Initialize counters
set /a SUCCESS_COUNT=0
set /a ERROR_COUNT=0

REM Process all JSON files
for %%f in ("%INPUT_DIR%\*.json") do (
    set "INPUT_FILE=%%f"
    set "FILENAME=%%~nf"
    set "OUTPUT_FILE=%OUTPUT_DIR%\!FILENAME!.csv"
    
    echo [%date% %time%] Processing: %%~nxf
    
    java -jar "%JAR_FILE%" "!INPUT_FILE!" "!OUTPUT_FILE!"
    
    if !errorlevel! equ 0 (
        echo [%date% %time%] SUCCESS: Converted %%~nxf
        set /a SUCCESS_COUNT+=1
    ) else (
        echo [%date% %time%] ERROR: Failed to convert %%~nxf
        set /a ERROR_COUNT+=1
    )
)

echo.
echo [%date% %time%] Batch conversion completed
echo [%date% %time%] Successful conversions: %SUCCESS_COUNT%
echo [%date% %time%] Failed conversions: %ERROR_COUNT%

if %ERROR_COUNT% gtr 0 (
    exit /b 1
) else (
    exit /b 0
)