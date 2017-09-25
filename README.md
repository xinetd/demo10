# demo10
A simple Spring Boot REST based CRUD server

# design
+ Spring Boot (@RepositoryRestResource with default RepositoryRestHandlerMappings == basic CRUD operations)
+ Spring Data JPA 
+ H2 in memory DB
+ Spring MVC Tests (Middle ground between unit/integration test; basic CRUD tests)

# build
mvn package

# execute
mvn spring-boot:run or
java -jar target/demo10-0.0.1-SNAPSHOT.jar

# test
mvn test
