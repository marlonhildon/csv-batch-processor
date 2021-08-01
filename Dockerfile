FROM amazoncorretto:11
MAINTAINER marlon-hildon
COPY target/csv-batch-processor-0.0.1-SNAPSHOT.jar csv-batch-processor-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java","-jar","/csv-batch-processor-0.0.1-SNAPSHOT.jar"]