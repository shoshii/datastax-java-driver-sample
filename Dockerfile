FROM centos:7
RUN yum install -y java-11-openjdk-devel
RUN yum install -y maven
ADD ./pom.xml pom.xml
RUN mkdir -p /etc/datastax-driver/conf/
ADD ./application.conf /etc/datastax-driver/conf/application.conf
COPY ./src/ src
RUN mvn package -DskipTests clean
#ADD files/run.sh /run.sh

#CMD ["/run.sh"]