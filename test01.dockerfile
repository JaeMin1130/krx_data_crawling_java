FROM openjdk:17.0.1-jdk-slim

RUN apt-get -y update

RUN apt -y install wget

RUN apt -y install unzip

RUN apt -y install curl

RUN wget https://dl.google.com/linux/direct/google-chrome-stable_current_amd64.deb

RUN apt -y install ./google-chrome-stable_current_amd64.deb

RUN wget -O /tmp/chromedriver-linux64.zip https://storage.googleapis.com/chrome-for-testing-public/126.0.6478.61/linux64/chromedriver-linux64.zip

RUN unzip /tmp/chromedriver-linux64.zip -d /usr/bin

RUN rm /tmp/chromedriver-linux64.zip ./google-chrome-stable_current_amd64.deb

WORKDIR /app

ARG JAR_FILE=krx_data_crawling_java-1.0-SNAPSHOT.jar

COPY ${JAR_FILE} /app/krx_data_crawling.jar

ENTRYPOINT ["java", "-jar", "krx_data_crawling.jar"]