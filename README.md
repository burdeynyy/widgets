## Widgets API.

#### In order to run you have to have installed:
* Java 8
* Maven 3

#### The main technologies are:

* [Spring Boot 2](https://spring.io/projects/spring-boot) - Spring core, Data JPA (Hibernate), Web, Tests
* [H2 in-memory DB](http://www.h2database.com) - In-memory sql DB for 4th complication
* [Java spatial index](https://github.com/aled/jsi) - R-Tree index for 2nd complication
* [h2gis](http://www.h2gis.org/) - Spatial index - h2 extension for 2nd complication
* [bucket4j](https://github.com/vladimir-bukhtoyarov/bucket4j) - Token bucket implementation for 3rd complication


#### API 

All endpoints start with `/api` prefix, e.g. [http://localhost:8080/api/widgets](http://localhost:8080/api/widgets)

There is also swagger UI available for more convenient access to API [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

##### Run
using spring-boot maven plugin:
```
mvn spring-boot:run
```

...or building and running .jar
```
mvn clean install 
cd target  
java -jar widgets.jar 
```

##### Notes:
The project includes all four complications:
* 1\. Pagination - implemented with a little help of Spring [Pageable](https://docs.spring.io/spring-data/rest/docs/2.0.0.M1/reference/html/paging-chapter.html)
* 2\. Filtering - search for widgets entirely falling into the searched area - done by using spatial indices (cf. jsi and h2gis on the links above).
* 3\. Rate limiting - works with the token bucket algorithm under the hood. Rate limits are configured for all APIs inside `com.miro.web.controller` package. 
It could be implemented by introducing some annotation and applying to particular controller method, but I wanted to try solving it more generally...
* 4\. SQL Database - in addition to native implementation of storage, in-memory implementation (H2 DB based) has been added. Access and operations on the database are provided by Spring Data JPA, Hibernate. 
Flyway was used to create db structure and populate it with the initial data. In order to switch storage implementation change `app.storage` parameter in [application.yml](src/main/resources/application.yml).

Tests were written for repositories (unit-tests) and there are also integration tests for the APIs. 

##### Overall tests coverage :

| Class, %       | Method, %            | Line, %          |
|----------------|----------------------|-------------------
| 93% (31/ 33)   | 90% (119/ 131)       | 88% (379/ 426)   |

##### Initial test data :)
![GitHub Logo](test_data.jpg)


