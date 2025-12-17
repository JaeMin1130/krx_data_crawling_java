FROM openjdk:17.0.1-jdk-slim

ENV TZ=Asia/Seoul

# Update and install necessary tools
RUN apt-get update && apt-get -y install wget unzip curl less firefox-esr && rm -rf /var/lib/apt/lists/*
RUN wget -O /tmp/geckodriver-v0.34.0-linux64.tar.gz https://github.com/mozilla/geckodriver/releases/download/v0.34.0/geckodriver-v0.34.0-linux64.tar.gz
RUN tar -xzf /tmp/geckodriver-v0.34.0-linux64.tar.gz -C /usr/bin
RUN rm /tmp/geckodriver-v0.34.0-linux64.tar.gz
RUN ln -sf /usr/share/zoneinfo/Asia/Seoul /etc/localtime && \
    echo "Asia/Seoul" > /etc/timezone

# Set working directory
WORKDIR /app

# Define build argument and copy the JAR file into the container
ARG JAR_FILE=krx_data_crawling_java-1.0-SNAPSHOT.jar
ARG RESOURCES=resources
ARG VIMRC=.vimrc

COPY ${JAR_FILE} /app/krx_data_crawling.jar
