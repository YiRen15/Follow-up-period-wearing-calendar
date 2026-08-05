@echo off
chcp 65001 >nul
title 随访周期佩戴日历及规则生成测试系统

echo =======================================================
echo   正在启动随访周期佩戴日历客户端 (Windows 端)
echo =======================================================

set "WORK_DIR=%~dp0"
set "JAR_PATH=%WORK_DIR%target\Follow-up-period-wearing-calendar-1.0.0-jar-with-dependencies.jar"
set "EMBEDDED_JAVA=%WORK_DIR%jre\bin\java.exe"

if exist "%EMBEDDED_JAVA%" (
    echo [提示] 检测到本地自带免安装 JRE，正在启动程序...
    "%EMBEDDED_JAVA%" -jar "%JAR_PATH%"
    goto END
)

where java >nul 2>nul
if %errorlevel% equ 0 (
    echo [提示] 正在使用系统的 Java 环境启动程序...
    java -jar "%JAR_PATH%"
    goto END
)

echo.
echo 【错误提示】当前 Windows 电脑尚未找到可用的 Java 运行环境 (JRE)。
echo 解决方法：
echo 1. 请安装 Java 8 或更高版本 JRE/JDK。
echo 2. 或在本项目文件夹中放入免安装的 'jre' 文件夹。
echo.
pause

:END
