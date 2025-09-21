# Scientific Data Integration System - Usage Examples

## Direct Command Line Usage

### Single File Conversion
```bash
# Basic conversion
java -jar target/dataintegration-1.0-SNAPSHOT.jar data/input/mini_cooper_cars.json data/output/mini_cooper_cars.csv

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

## Docker Integration

### Dockerfile Example
```dockerfile
FROM openjdk:17-jre-slim

WORKDIR /app

# Copy the JAR file
COPY target/dataintegration-1.0-SNAPSHOT.jar app.jar

# Create data directories
RUN mkdir -p /app/data/input /app/data/output

# Copy scripts
COPY scripts/ /app/scripts/

# Make scripts executable
RUN chmod +x /app/scripts/*.py

# Default command
CMD ["java", "-jar", "app.jar"]
```

### Docker Compose
```yaml
version: '3.8'

services:
  data-integration:
    build: .
    volumes:
      - ./data/input:/app/data/input:ro
      - ./data/output:/app/data/output
    environment:
      - INPUT_DIR=/app/data/input
      - OUTPUT_DIR=/app/data/output
    command: ["python", "/app/scripts/automate.py", "/app/data/input", "/app/data/output"]
```

## CI/CD Integration Examples

### Jenkins Pipeline
```groovy
pipeline {
    agent any
    
    stages {
        stage('Build') {
            steps {
                sh 'mvn clean package -DskipTests'
            }
        }
        
        stage('Convert Data') {
            steps {
                script {
                    def inputDir = params.INPUT_DIRECTORY ?: 'data/input'
                    def outputDir = params.OUTPUT_DIRECTORY ?: 'data/output'
                    
                    sh """
                        python scripts/automate.py ${inputDir} ${outputDir} --validate --verbose
                    """
                }
            }
        }
        
        stage('Archive Results') {
            steps {
                archiveArtifacts artifacts: 'data/output/*.csv', fingerprint: true
            }
        }
    }
}
```

### Azure DevOps Pipeline
```yaml
trigger:
- main

pool:
  vmImage: 'ubuntu-latest'

variables:
  inputDirectory: 'data/input'
  outputDirectory: 'data/output'

steps:
- task: JavaToolInstaller@0
  displayName: 'Install Java 17'
  inputs:
    versionSpec: '17'
    jdkArchitectureOption: 'x64'
    jdkSourceOption: 'PreInstalled'

- script: |
    mvn clean package -DskipTests
  displayName: 'Build Application'

- script: |
    python scripts/automate.py $(inputDirectory) $(outputDirectory) --validate
  displayName: 'Convert JSON to CSV'

- task: PublishBuildArtifacts@1
  displayName: 'Publish CSV Files'
  inputs:
    pathToPublish: '$(outputDirectory)'
    artifactName: 'csv-files'
```

## API Integration (Future Enhancement)

### REST API Wrapper Example
```python
# Flask wrapper for the Java application
from flask import Flask, request, jsonify, send_file
import subprocess
import tempfile
import os

app = Flask(__name__)

@app.route('/convert', methods=['POST'])
def convert_json_to_csv():
    try:
        json_data = request.get_json()
        
        # Save JSON to temporary file
        with tempfile.NamedTemporaryFile(mode='w', suffix='.json', delete=False) as temp_json:
            json.dump(json_data, temp_json)
            temp_json_path = temp_json.name
        
        # Create temporary CSV file
        temp_csv_path = tempfile.mktemp(suffix='.csv')
        
        # Run conversion
        subprocess.run([
            'java', '-jar', 'target/dataintegration-1.0-SNAPSHOT.jar',
            temp_json_path, temp_csv_path
        ], check=True)
        
        # Return CSV file
        return send_file(temp_csv_path, as_attachment=True, download_name='converted.csv')
        
    except Exception as e:
        return jsonify({'error': str(e)}), 500
    finally:
        # Cleanup temporary files
        if 'temp_json_path' in locals():
            os.unlink(temp_json_path)
        if 'temp_csv_path' in locals() and os.path.exists(temp_csv_path):
            os.unlink(temp_csv_path)

if __name__ == '__main__':
    app.run(debug=True)
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

## Error Handling and Monitoring

### Log Analysis Script
```bash
# Monitor conversion logs
tail -f conversion.log | grep -E "(ERROR|SUCCESS|WARNING)"

# Generate daily report
grep "$(date +%Y-%m-%d)" conversion.log | grep -c "SUCCESS"
```

### Health Check Script
```bash
#!/bin/bash
# health-check.sh - Verify the application is working

TEST_INPUT="data/input/mini_cooper_cars.json"
TEST_OUTPUT="/tmp/health_check.csv"

if java -jar target/dataintegration-1.0-SNAPSHOT.jar "$TEST_INPUT" "$TEST_OUTPUT"; then
    echo "✅ Application health check passed"
    rm -f "$TEST_OUTPUT"
    exit 0
else
    echo "❌ Application health check failed"
    exit 1
fi
```