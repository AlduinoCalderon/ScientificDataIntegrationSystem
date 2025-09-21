# Scientific Data Integration System - Usage Examples

## Direct Command Line Usage

### Single File Conversion
```bash
# Basic conversion
java -jar target/dataintegration-1.0-SNAPSHOT.jar data/input/scientific_data.json data/output/scientific_data.csv

# With full paths
java -jar target/dataintegration-1.0-SNAPSHOT.jar "C:\data\input\scientific_data.json" "C:\data\output\scientific_data.csv"

# Interactive mode (prompts for file paths)
java -jar target/dataintegration-1.0-SNAPSHOT.jar
```

## Automation Scripts

### PowerShell (Windows)
```powershell
# Convert all JSON files in a directory
.\scripts\batch-convert.ps1 -InputDirectory "data\input" -OutputDirectory "data\output"

# With verbose logging
.\scripts\batch-convert.ps1 -InputDirectory "data\input" -OutputDirectory "data\output" -Verbose

# With custom JAR path
.\scripts\batch-convert.ps1 -InputDirectory "data\input" -OutputDirectory "data\output" -JarPath "custom\path\to\app.jar"
```

### Batch Script (Windows)
```cmd
REM Convert all JSON files in data\input to data\output
scripts\batch-convert.bat "data\input" "data\output"

REM Convert files from custom directories
scripts\batch-convert.bat "C:\MyData\JSON" "C:\MyData\CSV"
```

### Python Script
```bash
# Basic batch conversion
python scripts/automate.py data/input data/output

# With JSON validation
python scripts/automate.py data/input data/output --validate

# With verbose logging
python scripts/automate.py data/input data/output --verbose --validate

# With custom JAR path
python scripts/automate.py data/input data/output --jar-path custom/path/to/app.jar
```

## Scheduled Automation (Windows Task Scheduler)

### Create a Scheduled Task
```powershell
# Create a daily conversion task
$Action = New-ScheduledTaskAction -Execute "PowerShell.exe" -Argument "-ExecutionPolicy Bypass -File C:\path\to\ScientificDataIntegrationSystem\scripts\batch-convert.ps1 -InputDirectory 'C:\data\input' -OutputDirectory 'C:\data\output'"

$Trigger = New-ScheduledTaskTrigger -Daily -At "02:00AM"

$Settings = New-ScheduledTaskSettingsSet -AllowStartIfOnBatteries -DontStopIfGoingOnBatteries -StartWhenAvailable

Register-ScheduledTask -TaskName "ScientificDataConversion" -Action $Action -Trigger $Trigger -Settings $Settings -Description "Daily JSON to CSV conversion"
```


## Quick Start Commands

```bash
# 1. Build the application
mvn clean package -DskipTests

# 2. Test with sample data
java -jar target/dataintegration-1.0-SNAPSHOT.jar data/input/mini_cooper_cars.json data/output/test_output.csv

# 3. Batch convert all files
python scripts/automate.py data/input data/output --validate

# 4. Schedule daily conversion (Windows)
schtasks /create /tn "DataConversion" /tr "python C:\path\to\scripts\automate.py C:\data\input C:\data\output" /sc daily /st 02:00
```