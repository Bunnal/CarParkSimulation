#!/bin/bash

# =============================================================================
# Car Park Management Sim - Quick Start Script
# =============================================================================

echo "🚗 Car Park Management Sim - Quick Start"
echo "======================================="
echo ""

# Check Java version
echo "Checking Java version..."
java -version 2>&1 | head -n 1

if [ $? -ne 0 ]; then
    echo "❌ Error: Java is not installed or not in PATH"
    echo "Please install Java 17 or higher"
    exit 1
fi

# Check Maven
echo ""
echo "Checking Maven..."
mvn -version 2>&1 | head -n 1

if [ $? -ne 0 ]; then
    echo "❌ Error: Maven is not installed or not in PATH"
    echo "Please install Maven 3.6 or higher"
    exit 1
fi

echo ""
echo "✅ Prerequisites satisfied"
echo ""

# Clean and compile
echo "📦 Cleaning and compiling project..."
mvn clean compile

if [ $? -ne 0 ]; then
    echo ""
    echo "❌ Compilation failed. Please check the error messages above."
    exit 1
fi

echo ""
echo "✅ Compilation successful!"
echo ""

# Run the application
echo "🚀 Launching Car Park Management Sim..."
echo ""
echo "   Press Ctrl+C to stop the application"
echo ""

java -cp target/classes Main
