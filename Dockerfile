# Stage 1: build frontend
FROM node:20 AS frontend-build
WORKDIR /app/smart-skills-webapp
COPY smart-skills-webapp/package*.json ./
RUN npm install
COPY smart-skills-webapp/ ./
RUN npm run build

# Stage 2: build backend
FROM maven:3.9.9-eclipse-temurin-21 AS backend-build
WORKDIR /app/smart-skills-backend
COPY smart-skills-backend/pom.xml ./
COPY smart-skills-backend/.mvn .mvn
COPY smart-skills-backend/mvnw ./
RUN chmod +x mvnw || true
RUN ./mvnw -q -DskipTests dependency:go-offline || mvn -q -DskipTests dependency:go-offline

COPY smart-skills-backend/src ./src
COPY --from=frontend-build /app/smart-skills-webapp/dist ./src/main/resources/static

RUN ./mvnw clean package -DskipTests || mvn clean package -DskipTests

# Stage 3: run app
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=backend-build /app/smart-skills-backend/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]