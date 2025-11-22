# Quick Start Guide

## Windows Users - 3 Simple Steps

### Step 1: Install Prerequisites
- Install Java 11+ from https://adoptium.net/
- Install Maven from https://maven.apache.org/download.cgi
- Add both to your Windows PATH

### Step 2: First Time Setup
Open Command Prompt in the mainbank folder and run:
```cmd
mvn clean compile
```

### Step 3: Run the Application
Double-click `run.bat` OR run in Command Prompt:
```cmd
mvn javafx:run
```

## First Login
1. Click "Need an account? Sign up"
2. Fill in your details
3. Login with your credentials
4. Start managing customers and accounts!

## Troubleshooting
- **Database Error?** Run `reset-database.bat` to start fresh
- **Maven not found?** Make sure Maven is in your PATH
- **Java not found?** Install Java 11 or higher

## Need Help?
See [WINDOWS_SETUP.md](WINDOWS_SETUP.md) for detailed instructions.
