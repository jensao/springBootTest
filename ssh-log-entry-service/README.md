# spring boot test


## Maven wrapper

 * __`:> ./mvnw spring-boot:run`__ - Starts the application
 * __`:> ./mvnw clean package`__ - To to package the artifact in to an uber-jar 


## A curl example

For the __sshLog__ endpoint
 * __`:> curl -i http://localhost:8080/sshlog?page=2`__
 * __`:> curl -i http://localhost:8080/sshlog/search?page=1&loggedIn=false&userName=root`__
 * __`:> curl -i -H "Content-Type: application/json" -X POST -d '{"ipNumber":"192.168.1.1", "date":"2016-10-05T12:13:14", "userName":"root", "loggedIn":"false"}' http://localhost:8080/sshlog`__
 * __`:> curl -i http://localhost:8080/sshlog/1`__


## To create the MySQL database
```
CREATE DATABASE ssh_log CHARACTER SET utf8 COLLATE utf8_unicode_ci;
```

```
 create table ssh_log_entry (
    id bigint not null auto_increment, 
    date tinyblob not null, 
    ip_number tinyblob not null, 
    logged_in bit not null, 
    user_name varchar(100) not null, 
    primary key (id)
    ) CHARACTER SET=utf8 DEFAULT COLLATE utf8_general_ci;
```

Jag förändrade även följande i **my.ini**
```
[client]
default-character-set   = utf8

[mysql]
default-character-set   = utf8

[mysqld]
bind-address            = 0.0.0.0
character-set-server    = utf8mb4
collation-server        = utf8mb4_general_ci
```

se: `mysql> show variables where variable_name like '%char%';`

## Länkar ##

 * [spring boot reference Guide](http://docs.spring.io/spring-boot/docs/current-SNAPSHOT/reference/htmlsingle/)  Reference Guide
 * [spring boot samples](https://github.com/spring-projects/spring-boot/tree/master/spring-boot-samples) från github
 * [spring mvc](http://docs.spring.io/spring/docs/current/spring-framework-reference/html/mvc.html)
 * [Integrations tester](https://spring.io/blog/2016/04/15/testing-improvements-in-spring-boot-1-4) med spring boot
 * **Dynamic queries, using query by example** (QBE) betydligt lättare att använda än Specification<T> nedan dock inte riktigt lika flexibelt
   - se [docs.spring.io](http://docs.spring.io/spring-data/jpa/docs/current/reference/html/#query-by-example)
 * **Dynamic queries, using the Specification<T>**  interface (which using the JPA Criteria API)
   - Se [Stackoverflow - filtering-database-rows...](http://stackoverflow.com/questions/20280708/filtering-database-rows-with-spring-data-jpa-and-spring-mvc)
   - Se [Stackoverflow - spring-data-jpa-query-by-example](http://stackoverflow.com/questions/27626825/spring-data-jpa-query-by-example)
   - Se [baeldung](http://www.baeldung.com/rest-api-search-language-spring-data-specifications) bra exempel där även >, <, : används i queryn
   - se [spring.io/blog/](https://spring.io/blog/2011/04/26/advanced-spring-data-jpa-specifications-and-querydsl/)
 * **TEST**
   - [Profile-specific properties](http://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#boot-features-external-config-profile-specific-properties)
 * [spring and localdate](http://lewandowski.io/2016/02/formatting-java-time-with-spring-boot-using-json/) - Hur man serialiserar / deserialiserar datum osv till Java 8 localDate(Time) även denna [stackoverflow](http://stackoverflow.com/questions/27952472/serialize-deserialize-java-8-java-time-with-jackson-json-mapper) är bra
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

## TODO if continue
 * TEST, TEST, TEST, e.g. more integration tests against the REST resources
 * Learn how to do better filtering e.g against the sshlog/search endpoint. See: [baeldung.com](http://www.baeldung.com/rest-api-search-language-spring-data-specifications), [stackoverflow](http://stackoverflow.com/questions/20280708/filtering-database-rows-with-spring-data-jpa-and-spring-mvc)
 * Better been validation

## Titta vidare på 
 * Klumpa ihop alla [Annoteringar](http://stackoverflow.com/questions/35845576/testpropertysource-annotation-alternative-on-a-method-level)
 
## Remove me and the code that supports it
For the __Greeting__ endpoint (Remove me soon)
 1. __`:> curl -i http://localhost:8080/greeting`__
 *  __`:> curl -i -H "Content-Type: application/json" -X POST -d '{"content":"FooooBarrr"}' http://localhost:8080/greeting`__
 *  __`:> curl -i http://localhost:8080/greeting/1`__
 *  __`:> curl -i -H "Content-Type: application/json" -X PATCH -d '{"content":"Updated content"}' http://localhost:8080/greeting/1`__
 *  __`:> curl -i -X DELETE http://localhost:8080/greeting/1`__