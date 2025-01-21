# 使用带有apt包管理器的OpenJDK 17镜像作为构建环境
FROM openjdk:17-jdk-slim AS build

# 设置工作目录
WORKDIR /app

# 将 Maven 的 pom.xml 和源代码复制到容器中
COPY pom.xml .
COPY src ./src

# 更新apt包列表并安装Maven
RUN apt-get update && \
    apt-get install -y maven && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

# 运行Maven构建
RUN mvn clean package -DskipTests

# 检查target目录
RUN ls /app/target

# 使用更小的JRE镜像来运行应用程序
FROM openjdk:17

# 设置工作目录
WORKDIR /app

# 从构建阶段复制JAR文件到运行阶段
COPY --from=build /app/target/*.jar app.jar

# 运行应用程序
ENTRYPOINT ["java", "-jar", "app.jar"]

# 暴露应用端口
EXPOSE 8081