# Jenkins construit le WAR via Maven (mvn clean package),
# puis Docker empaquète uniquement le runtime Tomcat + WAR.
FROM tomcat:9-jdk11-temurin

LABEL maintainer="ftutorials"
LABEL description="Webapp Java"

# Métadonnées de build injectées par la CI (docker build --build-arg ...).
# Les valeurs par défaut servent de repli pour un build/exécution en local,
# hors pipeline. Exposées en ENV pour être lues par la webapp au runtime.
ARG IMAGE_TAG=latest
ARG BUILD_NUMBER=local
ENV IMAGE_TAG=${IMAGE_TAG} \
    BUILD_NUMBER=${BUILD_NUMBER}

# Suppression des webapps par défaut de Tomcat
RUN rm -rf /usr/local/tomcat/webapps/*

# Copie du WAR buildé par Jenkins (webapp/target/webapp.war)
COPY webapp/target/webapp.war /usr/local/tomcat/webapps/ROOT.war

EXPOSE 8080

CMD ["catalina.sh", "run"]
