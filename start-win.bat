@echo off
chcp 65001 >nul
title 随访周期佩戴日历客户端 (Windows)

echo =======================================================
echo   正在启动随访周期佩戴日历客户端 (Windows 端)
echo =======================================================
echo.

set "WORK_DIR=%~dp0"
set "JAR_PATH=%~dp0target\Follow-up-period-wearing-calendar-1.0.0-jar-with-dependencies.jar"
set "EMBEDDED_JAVA=%~dp0jre\bin\java.exe"

rem 1. 检查是否存在 Jar 文件
if exist "%JAR_PATH%" goto CHECK_JAVA
echo 【错误提示】未找到可执行的 Jar 文件！
echo 期待路径：%JAR_PATH%
echo 请确保 target 目录下包含 Follow-up-period-wearing-calendar-1.0.0-jar-with-dependencies.jar
echo.
pause
exit /b 1

:CHECK_JAVA
rem 2. 优先尝试本地自带的 JRE
if not exist "%EMBEDDED_JAVA%" goto CHECK_SYSTEM_JAVA
echo [提示] 检测到本地内置免安装 JRE，正在启动程序...
"%EMBEDDED_JAVA%" -jar "%JAR_PATH%"
if %errorlevel% neq 0 (
    echo.
    echo 【运行错误】程序运行异常结束，错误码：%errorlevel%
    pause
)
goto END

:CHECK_SYSTEM_JAVA
rem 3. 检查系统的 Java
where java >nul 2>nul
if %errorlevel% neq 0 goto NO_JAVA
echo [提示] 正在使用系统 Java 环境启动程序...
java -jar "%JAR_PATH%"
if %errorlevel% neq 0 (
    echo.
    echo 【运行错误】程序运行异常结束，错误码：%errorlevel%
    pause
)
goto END

:NO_JAVA
echo.
echo 【错误提示】当前 Windows 电脑尚未找到可用的 Java 运行环境 (JRE)。
echo.
echo 解决方法：
echo 1. 请在该电脑上安装 Java 8 或更高版本 (JRE/JDK)。
echo 2. 或在本项目根目录下放一个免安装的 'jre' 文件夹即可直接运行。
echo.
pause

:END
