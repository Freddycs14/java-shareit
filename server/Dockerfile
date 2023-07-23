FROM amazoncorretto:11
#      ENV TZ=America/Mexico_City
COPY target/*.jar server.jar
ENTRYPOINT ["java", "-jar","/server.jar"]
