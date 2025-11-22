@echo off
echo Resetting database...
if exist data (
    rmdir /s /q data
    echo Database deleted successfully!
) else (
    echo No database found.
)
echo.
echo Database will be recreated when you run the application.
pause
