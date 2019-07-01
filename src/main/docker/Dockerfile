FROM registry.avantiplc.net/avanti/java:2
MAINTAINER Navid Ghahremani <ghahramani.navid@gmail.com>

ENV SPRING_OUTPUT_ANSI_ENABLED=ALWAYS
ENV JAVA_OPTS=""
ENV PYTHONIOENCODING=utf8
ENV PYTHONUNBUFFERED=false

ARG USER=docker
ARG USER_ID=1000
ARG HOME=/home/$USER
ARG FILE_BEAT_VERSION=filebeat-6.5.4-linux-x86_64

ENV FILE_BEAT_HOME=$HOME/filebeat

# Add a docker user to run our application so that it doesn't need to run as root
RUN groupadd $USER -g $USER_ID
RUN useradd $USER -u $USER_ID -g $USER_ID -m -s /bin/bash

ADD https://artifacts.elastic.co/downloads/beats/filebeat/$FILE_BEAT_VERSION.tar.gz $HOME/filebeat.tar.gz

WORKDIR $HOME

RUN tar -xvf filebeat.tar.gz
RUN mv $FILE_BEAT_VERSION $FILE_BEAT_HOME
RUN rm -rf filebeat.tar.gz

COPY ./filebeat.yml $FILE_BEAT_HOME/filebeat.yml
RUN chmod go-w $FILE_BEAT_HOME/filebeat.yml

COPY ./wait-for-it.sh $HOME/wait-for-it.sh
RUN chmod +x $HOME/wait-for-it.sh

RUN chown -R $USER:$USER $FILE_BEAT_HOME
RUN chmod +x $FILE_BEAT_HOME/filebeat

COPY ./entrypoint.sh /entrypoint.sh
RUN chmod 755 /entrypoint.sh && chown $USER:$USER /entrypoint.sh

COPY ./Procfile /Procfile

COPY ./healthcheck.jar /healthcheck.jar
HEALTHCHECK --interval=12s --timeout=12s --start-period=30s CMD java -jar /healthcheck.jar http://localhost:8080/actuator/health

USER $USER

COPY ./*.war app.war

EXPOSE 8080

ENTRYPOINT ["/bin/sh", "/entrypoint.sh"]
