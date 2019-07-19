FROM openjdk:8
ADD target/Events-1.0.jar Events-1.0.jar
EXPOSE 8085
ENTRYPOINT ["java", "-jar", "Events-1.0.jar"]