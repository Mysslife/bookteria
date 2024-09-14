# Stage 1: Build: giống như project java, muốn chạy được thì trước tiên phải build/đóng gói/package
# Start with maven image that includes JDK
# Đây "maven@3.9.8-amazoncorretto-21" là image có sẵn theo version của Maven trên docker hub
# AS build: có nghĩa đặt tên cho image này là build
# Mỗi cái FROM là 1 image. Hiện có 2 images trong Dockerfile này
FROM maven:3.9.8-amazoncorretto-21 AS build

# Copy source code and pom.xml file to /app folder
# folder source để copy nằm cùng cấp với Dockerfile
# Mỗi một image có 1 hệ điều hành riêng. Nên ở đây tạo workdir app, ở dưới cũng tạo, nhưng không liên quan vì 2 images
# khác nhau và mỗi cái có os cho nhau riêng
WORKDIR /app
COPY pom.xml .
COPY src ./src

# Build source code with maven cmd
RUN mvn package -DskipTests

# -----------------------------------------------------
# Stage 2: create image
# Start with Amazon Corretto JDK 21 - Ở trên build maven, dưới build jdk tương thích
FROM amazoncorretto:21.0.4

# Set working folder to App and Copy complied file from above step
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

# Command to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
