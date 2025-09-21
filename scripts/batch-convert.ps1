# Scientific Data Integration Automation Script
# PowerShell script for automated JSON to CSV conversion

param(
    [Parameter(Mandatory=$true)]
    [string]$InputDirectory,
    
    [Parameter(Mandatory=$true)]
    [string]$OutputDirectory,
    
    [string]$JarPath = "target/dataintegration-1.0-SNAPSHOT.jar",
    
    [switch]$Verbose
)

# Function to log messages
function Write-Log {
    param([string]$Message, [string]$Level = "INFO")
    $timestamp = Get-Date -Format "yyyy-MM-dd HH:mm:ss"
    $logMessage = "[$timestamp] [$Level] $Message"
    Write-Host $logMessage
    
    # Optional: Write to log file
    # $logMessage | Add-Content -Path "conversion.log"
}

# Main conversion function
function Convert-JsonFiles {
    param(
        [string]$InputDir,
        [string]$OutputDir,
        [string]$JarFile
    )
    
    Write-Log "Starting batch JSON to CSV conversion"
    Write-Log "Input Directory: $InputDir"
    Write-Log "Output Directory: $OutputDir"
    
    # Validate JAR file exists
    if (-not (Test-Path $JarFile)) {
        Write-Log "JAR file not found: $JarFile" "ERROR"
        exit 1
    }
    
    # Create output directory if it doesn't exist
    if (-not (Test-Path $OutputDir)) {
        New-Item -ItemType Directory -Path $OutputDir -Force | Out-Null
        Write-Log "Created output directory: $OutputDir"
    }
    
    # Get all JSON files in input directory
    $jsonFiles = Get-ChildItem -Path $InputDir -Filter "*.json" -File
    
    if ($jsonFiles.Count -eq 0) {
        Write-Log "No JSON files found in $InputDir" "WARNING"
        return
    }
    
    Write-Log "Found $($jsonFiles.Count) JSON files to process"
    
    $successCount = 0
    $errorCount = 0
    
    foreach ($jsonFile in $jsonFiles) {
        $inputPath = $jsonFile.FullName
        $outputFileName = [System.IO.Path]::GetFileNameWithoutExtension($jsonFile.Name) + ".csv"
        $outputPath = Join-Path $OutputDir $outputFileName
        
        Write-Log "Processing: $($jsonFile.Name) -> $outputFileName"
        
        try {
            # Run the Java application
            $process = Start-Process -FilePath "java" -ArgumentList @(
                "-jar", $JarFile, $inputPath, $outputPath
            ) -Wait -PassThru -WindowStyle Hidden
            
            if ($process.ExitCode -eq 0) {
                Write-Log "Successfully converted: $($jsonFile.Name)" "SUCCESS"
                $successCount++
            } else {
                Write-Log "Conversion failed for: $($jsonFile.Name) (Exit code: $($process.ExitCode))" "ERROR"
                $errorCount++
            }
        }
        catch {
            Write-Log "Exception processing $($jsonFile.Name): $($_.Exception.Message)" "ERROR"
            $errorCount++
        }
    }
    
    Write-Log "Batch conversion completed"
    Write-Log "Successful conversions: $successCount"
    Write-Log "Failed conversions: $errorCount"
    
    return @{
        Success = $successCount
        Errors = $errorCount
        Total = $jsonFiles.Count
    }
}

# Execute the conversion
try {
    $result = Convert-JsonFiles -InputDir $InputDirectory -OutputDir $OutputDirectory -JarFile $JarPath
    
    if ($result.Errors -gt 0) {
        Write-Log "Some conversions failed. Check the logs above for details." "WARNING"
        exit 1
    } else {
        Write-Log "All conversions completed successfully!" "SUCCESS"
        exit 0
    }
}
catch {
    Write-Log "Script execution failed: $($_.Exception.Message)" "ERROR"
    exit 1
}