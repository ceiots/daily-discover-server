# Dockerfile for daily-discover-server
# 直接复制本地预构建的 JAR（避免容器内编译、基础镜像拉取慢）
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY target/daily-discover-server-1.0.0-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]