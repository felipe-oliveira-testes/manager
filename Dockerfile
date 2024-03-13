FROM openjdk:8

#define a work diretory
WORKDIR /app

#copy maven and pom to the work directory
COPY .mvn/ .mvn
COPY mvnw pom.xml ./

#install dependencies
RUN ./mvnw dependency:resolve

#copy source code to the path inside the work diretory
COPY src ./src

#define exposed port
EXPOSE 8080

CMD ["./mvnw", "spring-boot:run"]