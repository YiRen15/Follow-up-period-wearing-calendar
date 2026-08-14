#!/bin/bash
echo "=== 正在启动随访周期佩戴日历客户端 (Mac 端) ==="
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
JAR_PATH="$DIR/target/Follow-up-period-wearing-calendar-Fupwc-1.00.03-jar-with-dependencies.jar"

if [ ! -f "$JAR_PATH" ]; then
    echo "未找到打包好的 Jar 文件，正在尝试自动使用 Maven 进行编译打包..."
    mvn clean package -DskipTests
fi

if [ -f "$JAR_PATH" ]; then
    echo "成功找到 Jar 文件，正在启动程序..."
    java -jar "$JAR_PATH"
else
    echo "错误：打包失败，请确保本地已安装 JDK 8+ 与 Maven 环境。"
fi
