#!/usr/bin/env python3
"""
Scientific Data Integration System - Python Automation Script
Automates JSON to CSV conversion using the Java application.
"""

import os
import sys
import subprocess
import argparse
import logging
from pathlib import Path
from typing import List, Tuple
import json
from datetime import datetime

# Configure logging
logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s - %(levelname)s - %(message)s',
    handlers=[
        logging.StreamHandler(),
        logging.FileHandler('conversion.log')
    ]
)

logger = logging.getLogger(__name__)

class DataIntegrationAutomator:
    """Automates the JSON to CSV conversion process."""
    
    def __init__(self, jar_path: str = "target/dataintegration-1.0-SNAPSHOT.jar"):
        self.jar_path = Path(jar_path)
        if not self.jar_path.exists():
            raise FileNotFoundError(f"JAR file not found: {jar_path}")
    
    def convert_file(self, input_file: Path, output_file: Path) -> bool:
        """Convert a single JSON file to CSV."""
        try:
            cmd = [
                "java", "-jar", str(self.jar_path),
                str(input_file), str(output_file)
            ]
            
            logger.info(f"Converting: {input_file.name} -> {output_file.name}")
            
            result = subprocess.run(
                cmd, 
                capture_output=True, 
                text=True, 
                check=True
            )
            
            logger.info(f"✅ Successfully converted: {input_file.name}")
            return True
            
        except subprocess.CalledProcessError as e:
            logger.error(f"❌ Conversion failed for {input_file.name}")
            logger.error(f"Exit code: {e.returncode}")
            logger.error(f"Error output: {e.stderr}")
            return False
        except Exception as e:
            logger.error(f"❌ Unexpected error converting {input_file.name}: {e}")
            return False
    
    def batch_convert(self, input_dir: Path, output_dir: Path) -> Tuple[int, int]:
        """Convert all JSON files in a directory."""
        logger.info(f"Starting batch conversion")
        logger.info(f"Input directory: {input_dir}")
        logger.info(f"Output directory: {output_dir}")
        
        # Create output directory if it doesn't exist
        output_dir.mkdir(parents=True, exist_ok=True)
        
        # Find all JSON files
        json_files = list(input_dir.glob("*.json"))
        
        if not json_files:
            logger.warning(f"No JSON files found in {input_dir}")
            return 0, 0
        
        logger.info(f"Found {len(json_files)} JSON files to process")
        
        success_count = 0
        error_count = 0
        
        for json_file in json_files:
            output_file = output_dir / f"{json_file.stem}.csv"
            
            if self.convert_file(json_file, output_file):
                success_count += 1
            else:
                error_count += 1
        
        logger.info(f"Batch conversion completed")
        logger.info(f"Successful conversions: {success_count}")
        logger.info(f"Failed conversions: {error_count}")
        
        return success_count, error_count
    
    def validate_json_files(self, input_dir: Path) -> List[Path]:
        """Validate JSON files before conversion."""
        logger.info("Validating JSON files...")
        
        valid_files = []
        invalid_files = []
        
        for json_file in input_dir.glob("*.json"):
            try:
                with open(json_file, 'r', encoding='utf-8') as f:
                    json.load(f)
                valid_files.append(json_file)
                logger.debug(f"✅ Valid JSON: {json_file.name}")
            except json.JSONDecodeError as e:
                invalid_files.append(json_file)
                logger.error(f"❌ Invalid JSON: {json_file.name} - {e}")
            except Exception as e:
                invalid_files.append(json_file)
                logger.error(f"❌ Error reading {json_file.name}: {e}")
        
        logger.info(f"Validation complete: {len(valid_files)} valid, {len(invalid_files)} invalid")
        
        if invalid_files:
            logger.warning("Invalid files found. Consider fixing them before conversion:")
            for file in invalid_files:
                logger.warning(f"  - {file.name}")
        
        return valid_files

def main():
    """Main entry point for the automation script."""
    parser = argparse.ArgumentParser(
        description="Automate JSON to CSV conversion using Scientific Data Integration System"
    )
    
    parser.add_argument(
        "input_dir",
        type=str,
        help="Input directory containing JSON files"
    )
    
    parser.add_argument(
        "output_dir", 
        type=str,
        help="Output directory for CSV files"
    )
    
    parser.add_argument(
        "--jar-path",
        type=str,
        default="target/dataintegration-1.0-SNAPSHOT.jar",
        help="Path to the JAR file (default: target/dataintegration-1.0-SNAPSHOT.jar)"
    )
    
    parser.add_argument(
        "--validate",
        action="store_true",
        help="Validate JSON files before conversion"
    )
    
    parser.add_argument(
        "--verbose",
        action="store_true",
        help="Enable verbose logging"
    )
    
    args = parser.parse_args()
    
    if args.verbose:
        logging.getLogger().setLevel(logging.DEBUG)
    
    try:
        input_dir = Path(args.input_dir)
        output_dir = Path(args.output_dir)
        
        if not input_dir.exists():
            logger.error(f"Input directory does not exist: {input_dir}")
            return 1
        
        # Initialize the automator
        automator = DataIntegrationAutomator(args.jar_path)
        
        # Validate JSON files if requested
        if args.validate:
            valid_files = automator.validate_json_files(input_dir)
            if not valid_files:
                logger.error("No valid JSON files found. Exiting.")
                return 1
        
        # Perform batch conversion
        success_count, error_count = automator.batch_convert(input_dir, output_dir)
        
        # Generate summary report
        total_files = success_count + error_count
        success_rate = (success_count / total_files * 100) if total_files > 0 else 0
        
        logger.info("=" * 50)
        logger.info("CONVERSION SUMMARY")
        logger.info("=" * 50)
        logger.info(f"Total files processed: {total_files}")
        logger.info(f"Successful conversions: {success_count}")
        logger.info(f"Failed conversions: {error_count}")
        logger.info(f"Success rate: {success_rate:.1f}%")
        logger.info("=" * 50)
        
        return 1 if error_count > 0 else 0
        
    except Exception as e:
        logger.error(f"Script execution failed: {e}")
        return 1

if __name__ == "__main__":
    sys.exit(main())