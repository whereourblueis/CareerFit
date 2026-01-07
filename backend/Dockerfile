# Dockerfile

# Gradle과 JDK 17을 포함한 이미지를 기반으로 빌드 환경을 설정합니다.
FROM gradle:8.8.0-jdk17 AS build

# 작업 디렉터리를 생성합니다.
WORKDIR /app

# build.gradle 파일과 settings.gradle 파일을 먼저 복사하여 종속성을 캐싱합니다.
COPY build.gradle settings.gradle ./

# Gradle 종속성을 다운로드합니다.
RUN gradle build --no-daemon || return 0

# 전체 소스 코드를 복사합니다.
COPY . .

# Gradle을 사용하여 실행 가능한 JAR 파일을 빌드합니다.
RUN gradle bootJar --no-daemon

# 더 가볍고 안전한 JRE(Java Runtime Environment) 이미지를 기반으로 실행 환경을 설정합니다.
FROM openjdk:17-jdk-slim

# 작업 디렉터리를 생성합니다.
WORKDIR /app

# 빌드 단계에서 생성된 JAR 파일을 실행 단계로 복사합니다.
# build/libs/ 디렉터리에 있는 JAR 파일 중 가장 큰 파일을 app.jar로 복사합니다.
COPY --from=build /app/build/libs/*.jar app.jar

# 컨테이너 외부에서 8080 포트로 접근할 수 있도록 노출합니다.
EXPOSE 8080

# 컨테이너가 시작될 때 실행될 명령어를 지정합니다.
# 여기서 'prod' 프로파일을 활성화합니다.
ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=prod", "app.jar"]