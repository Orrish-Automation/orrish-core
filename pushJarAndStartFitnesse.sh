mvn clean compile assembly:single
mv target/orrish-core*.jar target/orrish-core.jar
curl -o fitnesse-standalone.jar "http://fitnesse.org/fitnesse-standalone.jar?responder=releaseDownload&release=20220319" -L
java -jar fitnesse-standalone.jar -e 0
