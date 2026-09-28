@echo off
chcp 65001 >nul
cd /d "%~dp0"

rem Автопоиск JDK в папке пользователя .jdks
for /d %%i in ("%USERPROFILE%\.jdks\*") do (
    if exist "%%i\bin\javac.exe" set "JAVA_HOME=%%i"
)

if not defined JAVA_HOME (
    echo JDK не найден в %USERPROFILE%\.jdks
    echo Установите JDK или укажите JAVA_HOME вручную.
    pause
    exit /b 1
)

set "PATH=%JAVA_HOME%\bin;%PATH%"

echo Используется JDK: %JAVA_HOME%
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