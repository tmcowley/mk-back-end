FROM openjdk:17-alpine
LABEL Thomas Cowley <tmcowley@pm.me>
EXPOSE 8080/tcp

RUN apk add --no-cache curl tar bash procps

# Downloading and installing Maven
# 1- Define a constant with the version of maven you want to install
ARG MAVEN_VERSION=3.8.4

# 3- Define the SHA (sha512sum) key to validate the maven download
ARG SHA=a9b2d825eacf2e771ed5d6b0e01398589ac1bfa4171f36154d1b5787879605507802f699da6f7cfc80732a5282fd31b28e4cd6052338cbef0fa1358b48a5e3c8

# 4- Define the URL where maven can be downloaded from
ARG BASE_URL=https://apache.osuosl.org/maven/maven-3/${MAVEN_VERSION}/binaries

# 5- Create the directories, download maven, validate the download, install it, remove downloaded file and set links
RUN mkdir -p /usr/share/maven /usr/share/maven/ref \
  && echo "Downlaoding maven" \
  && curl -fsSL -o /tmp/apache-maven.tar.gz ${BASE_URL}/apache-maven-${MAVEN_VERSION}-bin.tar.gz \
  \
  && echo "Checking download hash" \
  && echo "${SHA}  /tmp/apache-maven.tar.gz" | sha512sum -c - \
  \
  && echo "Unziping maven" \
  && tar -xzf /tmp/apache-maven.tar.gz -C /usr/share/maven --strip-components=1 \
  \
  && echo "Cleaning and setting links" \
  && rm -f /tmp/apache-maven.tar.gz \
  && ln -s /usr/share/maven/bin/mvn /usr/bin/mvn

# 6- Define environmental variables required by Maven, like Maven_Home directory and where the maven repo is located
ENV MAVEN_HOME /usr/share/maven
ENV MAVEN_CONFIG "$USER_HOME_DIR/.m2"

# CMD [""]

# copy back-end files to container
ADD ./ /

# package the back-end files
RUN mvn package; cd ..;

# get the jar-file location
ARG TARGET=target
ARG JAR_FILE=app-server-0.0.1-SNAPSHOT.jar
ARG JAR_FILE=${TARGET}/${JAR_FILE}

# copy resulting jar file to container top-level dir
RUN cp ${JAR_FILE} back-end.jar

ENTRYPOINT ["java","-jar","/back-end.jar"]
