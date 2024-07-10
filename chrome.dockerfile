FROM openjdk:17.0.1-jdk-slim

RUN apt-get -y update && \
    apt-get -y install wget unzip curl && \
    wget https://dl.google.com/linux/direct/google-chrome-stable_current_amd64.deb && \
    apt-get -y install ./google-chrome-stable_current_amd64.deb && \
    wget -O /tmp/chromedriver-linux64.zip https://storage.googleapis.com/chrome-for-testing-public/126.0.6478.61/linux64/chromedriver-linux64.zip && \
    unzip /tmp/chromedriver-linux64.zip -d /usr/bin && \
    rm /tmp/chromedriver-linux64.zip ./google-chrome-stable_current_amd64.deb

WORKDIR /app

ARG JAR_FILE=krx_data_crawling_java-1.0-SNAPSHOT.jar

COPY ${JAR_FILE} /app/krx_data_crawling.jar

ENTRYPOINT ["java", "-jar", "krx_data_crawling.jar"]
