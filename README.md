# spring boot test


## Maven wrapper

 * __`:> ./mvnw spring-boot:run`__ - Starts the application
 * __`:> ./mvnw clean package`__ - To to package the artifact in to an uber-jar 


## A curl example

 1. __`:> curl -i http://localhost:8080/greeting`__
 *  __`:> curl -i -H "Content-Type: application/json" -X POST -d '{"content":"FooooBarrr"}' http://localhost:8080/greeting`__
 *  __`:> curl -i http://localhost:8080/greeting/1`__
 *  __`:> curl -i -H "Content-Type: application/json" -X PATCH -d '{"content":"Updated content"}' http://localhost:8080/greeting/1`__
 *  __`:> curl -i -X DELETE http://localhost:8080/greeting/1`__
 

## Länkar ##

 * [spring](http://spring.io/)
 * [spring boot](http://projects.spring.io/spring-boot/)
 * [spring boot reference Guide](http://docs.spring.io/spring-boot/docs/current-SNAPSHOT/reference/htmlsingle/#howto) Reference Guide
 * [spring boot samples](https://github.com/spring-projects/spring-boot/tree/master/spring-boot-samples) från github
 * [spring mvc](http://docs.spring.io/spring/docs/current/spring-framework-reference/html/mvc.html) 
 * [metrics](http://localhost:8080/metrics/) se mer i [documentationen](http://docs.spring.io/spring-boot/docs/current/reference/html/production-ready-metrics.html)
 * [mappings](http://localhost:8080/mappings)
 * [health](http://localhost:8080/health)

## Komman igång och sätta upp nytt projekt 

 * Skapa projektstrukturen 
   - [Spring Initializr](http://start.spring.io/) web gui som genererar en zip fil. OBS klicka i de som behövs; Web, Security, Actuator
 * instalera spring CLI
   - Se framförallt [Installing the Spring Boot CLI](http://docs.spring.io/spring-boot/docs/current/reference/html/getting-started-installing-spring-boot.html#getting-started-installing-the-cli)
   - Eller välj en version på [spring-boot-cli](http://repo.spring.io/snapshot/org/springframework/boot/spring-boot-cli/) packa upp och läs  INSTALL.txt
 * spring plugin för Intellij finns inte i community versionen utan bara för betalversionen ("Ultimate")?
 * 
