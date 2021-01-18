FROM maven:3.6.3-jdk-11 as api
WORKDIR /usr/src/api
COPY pom.xml .
RUN mvn -B -f pom.xml -s /usr/share/maven/ref/settings-docker.xml dependency:resolve
COPY . .
RUN mvn -B -s /usr/share/maven/ref/settings-docker.xml package -DskipTests

FROM openjdk:11-jdk
RUN useradd -ms /bin/bash application
WORKDIR /backend

RUN chown application:application /backend
COPY --from=api /usr/src/api/target/cadttee-1.0.jar .

ENTRYPOINT exec java -Djava.security.egd=file:/dev/../dev/urandom $JAVA_OPTS -jar /backend/cadttee-1.0.jar