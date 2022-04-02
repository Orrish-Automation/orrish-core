FROM maven:3.5-jdk-8 AS build
WORKDIR /app
COPY . .
#Oracle JDBC Driver is not in maven repository, so we have to download and install manually
RUN curl -k -o ojdbc8-12.2.0.1.jar "https://maven.xwiki.org/externals/com/oracle/jdbc/ojdbc8/12.2.0.1/ojdbc8-12.2.0.1.jar" -L
RUN mvn install:install-file -Dfile=ojdbc8-12.2.0.1.jar -DgroupId="com.oracle.jdbc" -DartifactId="ojdbc8" -Dversion="12.2.0.1" -Dpackaging=jar
RUN mvn -f pom_with_ojdbc.xml clean compile assembly:single
RUN mv target/orrish-core*.jar target/orrish-core.jar

## Could not use alpine because of the issue https://github.com/microsoft/playwright-java/issues/556#issuecomment-897897345
FROM maven:3.5-jdk-8
WORKDIR /app
RUN mkdir "FitNesseRoot"
RUN mkdir "target"
COPY --from=build /app/target/*.jar target
COPY FitNesseRoot FitNesseRoot
RUN curl -o fitnesse-standalone.jar "http://fitnesse.org/fitnesse-standalone.jar?responder=releaseDownload&release=20220319" -L
EXPOSE 80
ENTRYPOINT ["java","-jar","fitnesse-standalone.jar","-e","0","-o"]
