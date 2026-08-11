@echo off
chcp 65001 >nul
title 随访周期佩戴日历及规则生成测试系统

echo =======================================================
echo   正在启动随访周期佩戴日历客户端 (Windows 端)
echo =======================================================

set "WORK_DIR=%~dp0"
set "JAR_PATH=%WORK_DIR%target\Follow-up-period-wearing-calendar-1.0.0-jar-with-dependencies.jar"
set "EMBEDDED_JAVA=%WORK_DIR%jre\bin\java.exe"

rem 1. 检查是否存在 Jar 文件，不存在则尝试使用 Maven 编译
if not exist "%JAR_PATH%" (
    echo.
    echo [提示] 未检测到打包好的 Jar 文件 (target 目录为空)。
    echo 正在尝试调用 Maven 自动编译生成 Jar 包...
    echo.
    call mvn clean package -DskipTests
    echo.
)

rem 2. 如果依然不存在 Jar，给出明确提示并暂停
if not exist "%JAR_PATH%" (
    echo.
    echo 【错误提示】未找到可执行的 Jar 文件！
    echo 错误原因：从 GitHub 下载的新源码默认不包含 target 编译文件。
    echo 解决方法：
    echo 1. 请确保本机安装了 Maven，并运行: mvn clean package -DskipTests
    echo 2. 编译成功后再运行本脚本或使用 Launch4j 打包。
    echo.
    pause
    exit /b 1
)

rem 3. 检查是否有本地免环境 JRE
if exist "%EMBEDDED_JAVA%" (
    echo [提示] 检测到本地自带免安装 JRE，正在启动程序...
    "%EMBEDDED_JAVA%" -jar "%JAR_PATH%"
    if %errorlevel% neq 0 (
        echo.
        echo 【异常中断】程序运行异常结束，错误码：%errorlevel%
        pause
    )
    goto END
)

rem 4. 检查是否有系统 Java 环境
where java >nul 2>nul
if %errorlevel% equ 0 (
    echo [提示] 正在使用系统的 Java 环境启动程序...
    java -jar "%JAR_PATH%"
    if %errorlevel% neq 0 (
        echo.
        echo 【异常中断】程序运行异常结束，错误码：%errorlevel%
        pause
    )
    goto END
)

echo.
echo 【错误提示】当前 Windows 电脑尚未找到可用的 Java 运行环境 (JRE)。
echo 解决方法：
echo 1. 请安装 Java 8 或更高版本 JRE/JDK。
echo 2. 或在本项目根目录下放入免安装的 'jre' 文件夹。
echo.
pause

:END
