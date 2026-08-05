@echo off
chcp 65001 >nul
echo =======================================================
echo   正在启动随访周期佩戴日历客户端 (Windows 端)
echo =======================================================

set JAR_PATH=%~dp0target\Follow-up-period-wearing-calendar-1.0.0-jar-with-dependencies.jar
set EMBEDDED_JAVA=%~dp0jre\bin\java.exe

rem 优先检测本地是否包含免安装 JRE
if exist "%EMBEDDED_JAVA%" (
    echo [提示] 检测到本地自带免环境 JRE，正在启动程序...
    "%EMBEDDED_JAVA%" -jar "%JAR_PATH%"
    goto END
)

rem 尝试检测 Maven 构建
if not exist "%JAR_PATH%" (
    echo [提示] 未找到打包好的 Jar 文件，正在尝试使用 Maven 编译打包...
    call mvn clean package -DskipTests
)

rem 检测系统环境变量中的 Java
where java >nul 2>nul
if %errorlevel% equ 0 (
    echo [提示] 正在使用系统的 Java 环境启动程序...
    java -jar "%JAR_PATH%"
) else (
    echo.
    echo 【错误提示】当前 Windows 电脑尚未安装 Java 运行环境 (JRE)。
    echo 解决方法：
    echo 1. 请安装 Java 8 或更高版本 JRE/JDK。
    echo 2. 或在本项目文件夹中放入一个免安装的 'jre' 文件夹即可直接免安装运行。
    echo.
    pause
)

:END
