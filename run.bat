@echo off
chcp 65001 >nul
cd /d "%~dp0"

echo Сборка проекта...
javac Main.java

if errorlevel 1 (
    echo Ошибка сборки!
    pause
    exit /b 1
)

echo Запуск эмулятора...
java Main

pause