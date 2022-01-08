FROM openjdk:17-alpine
EXPOSE 8080
RUN cd app-server; mvn package; cd ..;
ARG TARGET=app-server/target
ARG JAR_FILE=app-server-0.0.1-SNAPSHOT.jar
ARG JAR_FILE=${TARGET}/${JAR_FILE}
ADD ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]