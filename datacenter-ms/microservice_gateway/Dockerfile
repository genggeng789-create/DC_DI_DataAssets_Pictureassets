FROM core.harbor.cz.shenlan.com/library/openjdk:8-jdk-alpine
MAINTAINER datacenter
ADD target/gateway.jar gateway.jar
RUN echo "Asia/Shanghai" >/etc/timezone
RUN sh -c 'touch /gateway.jar'
ENV JAVA_OPTS=""
CMD exec java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar /gateway.jar
EXPOSE 8222