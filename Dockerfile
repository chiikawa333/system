# 构建阶段
FROM eclipse-temurin:17-jdk-alpine AS builder

WORKDIR /app

# 安装 Maven
RUN apk add --no-cache maven

# 复制 Maven 配置文件
COPY pom.xml .
COPY src ./src

# 执行 Maven 构建
RUN mvn clean package -DskipTests

# 运行阶段
FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

# 复制构建好的 jar 包
COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
