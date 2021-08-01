FROM amazoncorretto:11
MAINTAINER marlon-hildon
COPY target/csv-batch-processor-0.0.1-SNAPSHOT.jar csv-batch-processor-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java","-jar","/csv-batch-processor-0.0.1-SNAPSHOT.jar"]
ENV THREADS_FILES 1
ENV THREADS_AUX 0
CMD -Dspring-boot.run.arguments=--threads.files=${THREADS_FILES} --threads.aux=${THREADS_AUX}