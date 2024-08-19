FROM openjdk:17.0.1-jdk-slim

# Update and install necessary tools
RUN apt-get -y update 
RUN apt-get -y install wget unzip curl 
RUN apt-get -y install firefox-esr 
RUN wget -O /tmp/geckodriver-v0.34.0-linux64.tar.gz https://github.com/mozilla/geckodriver/releases/download/v0.34.0/geckodriver-v0.34.0-linux64.tar.gz 
RUN tar -xzf /tmp/geckodriver-v0.34.0-linux64.tar.gz -C /usr/bin 
RUN rm /tmp/geckodriver-v0.34.0-linux64.tar.gz

# Set working directory
WORKDIR /app

# Define build argument and copy the JAR file into the container
ARG JAR_FILE=krx_data_crawling_java-1.0-SNAPSHOT.jar
COPY ${JAR_FILE} /app/krx_data_crawling.jar

# Define the entry point for the container
ENTRYPOINT ["java", "-jar", "krx_data_crawling.jar"]
