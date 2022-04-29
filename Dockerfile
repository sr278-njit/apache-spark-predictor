FROM maven:3.6.0-jdk-11-slim
RUN apt-get update && apt-get install -y curl vim wget software-properties-common ssh net-tools ca-certificates
# RUN apt install -y maven
# RUN apt-get install -y --allow-unauthenticated openjdk-11-jdk
COPY apache-spark-predictor /usr/apache-spark-predictor/
EXPOSE 8080

# RUN find ${MAVEN_HOME}/boot/ -name plexus-classworlds-*.jar -type f -exec mv {} {}.old \;

ENV SPARK_WORKER_LOG=/usr/logs/app.log \
LOG_DIR=/usr/logs

RUN mkdir -p $LOG_DIR && \
touch $SPARK_WORKER_LOG && \
ln -sf /dev/stdout $SPARK_WORKER_LOG

WORKDIR /usr/apache-spark-trainer/
RUN chmod +x predictor-start.sh

WORKDIR /usr/apache-spark-trainer/
RUN mvn clean install

CMD ["/usr/apache-spark-predictor/predictor-start.sh"]