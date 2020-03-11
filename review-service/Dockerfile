#docker pull openjdk:14-jdk-slim
#docker pull openjdk:13.0.2-slim
#docker pull adoptopenjdk:13.0.2_8-jre-hotspot
#docker pull openjdk:13.0.2-jdk-slim
#docker pull adoptopenjdk:13.0.2_8-jdk-hotspot

FROM adoptopenjdk:13.0.2_8-jre-hotspot
LABEL maintainer="github.com/rnkoaa"
LABEL app="review-service"

WORKDIR /application

COPY entrypoint.sh /application/bin/
RUN chmod +x /application/bin/entrypoint.sh

# expose data for sqlite data
VOLUME /application/data
VOLUME /application/config

ENV appVersion="0.0.1-SNAPSHOT"

COPY build/libs/review-service-${appVersion}.jar /application/bin/app.jar

#ENTRYPOINT ["java", \
#"-Djava.security.egd=file:/dev/./urandom", \
#"-jar", \
#"/application/bin/app.jar", \
#"--spring.config.location=classpath:/default.properties,classpath:/application.yml,file:/application/config/application.yml"]
ENTRYPOINT ["/application/bin/entrypoint.sh"]