FROM openjdk:18
EXPOSE 5500:5500
ADD build/libs/moneytransferservice-0.0.1-SNAPSHOT.jar moneytransferservice.jar
ENTRYPOINT ["java","-jar","/moneytransferservice.jar"]