# Windows Setup Guide

## Prerequisites

1. **Java 11 or higher**
   - Download from: https://adoptium.net/
   - Verify installation: `java -version`

2. **Maven 3.6 or higher**
   - Download from: https://maven.apache.org/download.cgi
   - Add Maven to PATH
   - Verify installation: `mvn -version`

## Running the Application on Windows

### Method 1: Using the Batch File (Easiest)
1. Double-click `run.bat` in the project folder
2. The application will start automatically

### Method 2: Using Command Prompt
1. Open Command Prompt
2. Navigate to the project folder:
   ```cmd
   cd path\to\mainbank
   ```
3. Run the application:
   ```cmd
   mvn javafx:run
   ```

### Method 3: Using PowerShell
1. Open PowerShell
2. Navigate to the project folder:
   ```powershell
   cd path\to\mainbank
   ```
3. Run the application:
   ```powershell
   mvn javafx:run
   ```

## First Time Setup

1. **Compile the project** (first time only):
   ```cmd
   mvn clean compile
   ```

2. **Run the application**:
   ```cmd
   mvn javafx:run
   ```

3. **Create your account**:
   - Click "Need an account? Sign up"
   - Fill in your details
   - Login with your credentials

## Troubleshooting

### Database Error: "Column USER_ID not found"
This has been fixed. The database will automatically migrate when you run the application.

### JavaFX Not Found
Make sure you have Java 11+ installed. JavaFX is included via Maven dependencies.

### Maven Command Not Found
Add Maven to your Windows PATH:
1. Right-click "This PC" → Properties
2. Advanced system settings → Environment Variables
3. Add Maven's `bin` folder to PATH

### Port Already in Use
If you see database errors, close any other instances of the application.

## Database Location

The H2 database files are stored in:
```
mainbank\data\banking.mv.db
```

To reset the database, delete the `data` folder and restart the application.

## Building a JAR File

To create a standalone JAR:
```cmd
mvn clean package
```

The JAR will be in `target\banking-system-1.0.0.jar`
