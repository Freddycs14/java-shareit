FROM amazoncorretto:11
#      ENV TZ=America/Mexico_City
COPY target/*.jar gateway.jar
ENTRYPOINT ["java", "-jar","/gateway.jar"]