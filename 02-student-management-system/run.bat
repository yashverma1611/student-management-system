@echo off
title Student Management Desktop GUI Launcher
color 0a
cd /d "%~dp0"

echo =========================================================
echo       Starting Student Management System (Java Swing)
echo =========================================================
echo.

where javac >nul 2>nul
if %ERRORLEVEL% NEQ 0 (
    echo [NOTICE] Java JDK (javac) is not yet in your system PATH!
    echo.
    echo Opening instant interactive desktop Swing GUI simulator in your browser right now...
    start "" "demo\index.html"
    pause
    exit /b
)

where mvn >nul 2>nul
if %ERRORLEVEL% EQU 0 (
    mvn clean compile exec:java -Dexec.mainClass="com.sms.Main"
) else (
    echo Compiling Java Swing sources...
    if not exist "bin" mkdir "bin"
    javac -d "bin" src/main/java/com/sms/*/*.java src/main/java/com/sms/*.java
    java -cp "bin" com.sms.Main
)
pause
