FROM centos:7
RUN yum install -y java
RUN yum install -y maven
ADD ./pom.xml pom.xml
COPY ./src/ src
RUN mvn package clean
#ADD files/run.sh /run.sh

#CMD ["/run.sh"]