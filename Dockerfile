FROM cloudesire/java:8
MAINTAINER ClouDesire <dev@cloudesire.com>

RUN apt-get update && \
    apt-get install -yq --no-install-recommends vim unzip wget pwgen ca-certificates && \
    apt-get install -yq --no-install-recommends libtcnative-1 && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

ENV TOMCAT_MAJOR_VERSION 8
ENV TOMCAT_MINOR_VERSION 8.0.22
ENV CATALINA_HOME /tomcat
ENV JAVA_OPTS ""

# INSTALL TOMCAT
RUN wget -q https://archive.apache.org/dist/tomcat/tomcat-${TOMCAT_MAJOR_VERSION}/v${TOMCAT_MINOR_VERSION}/bin/apache-tomcat-${TOMCAT_MINOR_VERSION}.tar.gz && \
    wget -qO- https://archive.apache.org/dist/tomcat/tomcat-${TOMCAT_MAJOR_VERSION}/v${TOMCAT_MINOR_VERSION}/bin/apache-tomcat-${TOMCAT_MINOR_VERSION}.tar.gz.md5 | md5sum -c - && \
    tar zxf apache-tomcat-*.tar.gz && \
    rm apache-tomcat-*.tar.gz && \
    mv apache-tomcat* tomcat && \
    rm -fr tomcat/webapps/examples && \
    rm -fr tomcat/webapps/docs

ADD create_tomcat_admin_user.sh /create_tomcat_admin_user.sh
ADD run.sh /run.sh
RUN chmod +x /*.sh

ADD briccs-labels-webapp-1.0.5.zip /tomcat/webapps
RUN unzip /tomcat/webapps/briccs-labels-webapp-1.0.5.zip -d /tomcat/webapps

#RUN mkdir -p /usr/local/briccs-webapps/briccs-labels-webapp-1.0.5/WEB-INF/classes
#ADD templateSmall_v1.1 /usr/local/briccs-webapps/briccs-labels-webapp-1.0.5/WEB-INF/classes
#ADD templateBig_v1.1 /usr/local/briccs-webapps/briccs-labels-webapp-1.0.5/WEB-INF/classes
#ADD BRICCS_EDTA_BAG_v1.2 /usr/local/briccs-webapps/briccs-labels-webapp-1.0.5/WEB-INF/classes
#ADD briccs-labels-webapp.war /tomcat/webapps

EXPOSE 8080
CMD ["/run.sh"]
