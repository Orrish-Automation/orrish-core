FROM maven:3.5-jdk-8 AS build
WORKDIR /app
COPY . .
RUN curl -o ojdbc.jar.zip "http://www.java2s.com/Code/JarDownload/ojdbc6/ojdbc6.jar.zip" -L
RUN unzip -o ojdbc.jar.zip
RUN mvn install:install-file -Dfile=ojdbc6.jar -DgroupId="com.oracle" -DartifactId="ojdbc6" -Dversion="6.0.0" -Dpackaging=jar
RUN mvn compile assembly:single

## Could not use alpine because of the issue https://github.com/microsoft/playwright-java/issues/556#issuecomment-897897345
FROM maven:3.5-jdk-8
WORKDIR /app
RUN mkdir "FitNesseRoot"
RUN mkdir "target"
COPY --from=build /app/target/*.jar target
COPY FitNesseRoot FitNesseRoot
RUN curl -o fitnesse-standalone.jar "http://fitnesse.org/fitnesse-standalone.jar?responder=releaseDownload&release=20211030" -L
EXPOSE 80
ENTRYPOINT ["java","-jar","fitnesse-standalone.jar","-e","0","-o"]
