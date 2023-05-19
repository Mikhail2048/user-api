FROM adoptopenjdk/openjdk11@sha256:5a795ca759eaceab269bbe6207fa020725cbae3089604310daba3608b83a4919
COPY target/fat-jar.jar .
CMD [ "java", "-jar", "fat-jar.jar" ]