FROM eclipse-temurin:17-jre

#RUN curl -sSL https://sdk.cloud.google.com | bash
#ENV PATH $PATH:/root/google-cloud-sdk/bin

RUN mkdir -p /root/.config/gcloud
ADD ./application_default_credentials.json /root/.config/gcloud

RUN mkdir /app
WORKDIR /app

ADD ./api/target/ai-chat-api-1.0.0-SNAPSHOT.jar /app

EXPOSE 8080



CMD ["java", "-jar", "ai-chat-api-1.0.0-SNAPSHOT.jar"]
#ENTRYPOINT ["java", "-jar", "image-catalog-api-1.0.0-SNAPSHOT.jar"]
#CMD java -jar image-catalog-api-1.0.0-SNAPSHOT.jar
