## Flight Booking System

<p align="center">
    <a alt="Java">
        <img src="https://img.shields.io/badge/Java-v1.11-orange.svg" />
    </a>
    <a alt="Spring Boot">
        <img src="https://img.shields.io/badge/Spring%20Boot-v2.3.9-brightgreen.svg" />
    </a>
    <a alt="Dependencies">
        <img src="https://img.shields.io/badge/dependencies-up%20to%20date-brightgreen.svg" />
    </a>
    <a alt="Contributions">
        <img src="https://img.shields.io/badge/contributions-welcome-orange.svg" />
    </a>
    <a alt="Licence">
        <img src="https://img.shields.io/github/license/tascigorkem/flight-booking-api">
    </a>
</p>

HATEOAS Driven Rest Api - Microservice Architecture with Spring Cloud, Docker, Kafka and ELK.

### Project Structure

<img src="./docs/project-structure.jpg" alt="" width="800">

This is a project structure.

All applications in the project are dockerized.

In the center of this schema, you can see Eureka Discovery Server from Spring Cloud Netflix and 2 Spring Boot microservices registered to this server. The first microservice is Restful CRUD API, and it is uses PostgreSQL as a database. The second microservice is a mail sending service, and it is uses MailHog for SMTP testing.

<img src="./docs/eureka.jpg" alt="" width="600">

In addition to these, 2 microservices communicate with each other with Apache Kafka and Zookeeper. API service sends message to Kafka and mail sending service receives this message and sends it by e-mail.

Also, elastic stack used for log-analysis solution. Filebeats binds the dockerized container logs and ships these to ElasticSearch. Then, Kibana visualize these logs.


### HATEOAS Driven REST API

HATEOAS (Hypermedia as the Engine of Application State) is a constraint of the REST application architecture that lets us use the hypermedia links in the response contents. It allows the client can dynamically navigate to the appropriate resources by traversing the hypermedia links.

JSON Hypermedia API Language (HAL) is a response's content-type: application/hal+json with pagination.

<img src="./docs/hateoas-1.jpg" alt="" width="600">

<img src="./docs/hateoas-2.jpg" alt="" width="600">


### Docker

All applications in this project are dockerized. To achieve this, 3 docker-compose files are created.

Then, all applications can be started with a single docker-compose command:

`
docker-compose -f docker-compose-app.yml -f docker-compose-elk.yml -f docker-compose-kafka.yml up --build -d
`

<img src="./docs/docker-compose.jpg" alt="" width="600">

PS: For local development, up docker-compose-kafka-local.yml, then start microservices.

`
docker-compose -f docker-compose-kafka-local.yml up -d
`


### Test & Validation

JUnit5, Mockito and Github-Faker are used in this project.

This snippet shows that unit test for Get by id request and usage of Arrange-Act-Assert (3A) pattern. Additionally, checks status of response.

<img src="./docs/code-snippet-test-get-by-id.jpg" alt="" width="600">

This snippet checks whether expected and returned objects are equal according to the fields.

<img src="./docs/code-snippet-test-fields.jpg" alt="" width="600">

This snippet checks whether invalid request returns the HTTP status 400 Bad Request.

<img src="./docs/code-snippet-test-400-bad-request.jpg" alt="" width="600">

<img src="./docs/400-bad-request-postman.jpg" alt="" width="600">


### Database

Used PostgreSQL as database.

DB Diagram:

<img src="./docs/db-diagram.jpg" alt="" width="800">


`spring.datasource.initialization-mode:always` for database initialization from schema.sql

Database Schema Generation - Sql: `./flight-booking-service/src/main/resources/schema.sql`

Database Inserts - Sql: ./flight-booking-service/src/main/resources/db-inserts.sql`

BaseEntity class with `@MappedSuperClass` annotation and is used by all entity classes as a super class. BaseEntity consists of id, creationTimestamp, updateTimestamp and deletionTimestamp fields.

Additionally, there are ManyToOne and OneToMany relations between entities.

<img src="./docs/flight-entity-1.jpg" alt="" width="600">
<img src="./docs/flight-entity-2.jpg" alt="" width="600">


### Kafka & ElasticSearch

**Kafka Producer** in microservice 1(flight-booking-service), that sends the email message  to Kafka.

<img src="./docs/kafka-producer.jpg" alt="" width="600">

**Kafka Consumer** in microservice 2(mail-service), that receives the published email message from Kafka and sends it by e-mail.

<img src="./docs/kafka-consumer.jpg" alt="" width="600">

MailHog, that catches e-mail was sent by microservice 2(mail-service).

<img src="./docs/mailhog.jpg" alt="" width="600">

Additionally, this whole process can be monitored and analyzed through Kibana with Elasticsearch.

<img src="./docs/kibana.jpg" alt="" width="800">


### Docs

Docs located `./docs` folder in the main project folder.


#### Javadoc

Used javadoc standards for method comments.

Run `mvn javadoc:javadoc` for create javadocs.

<img src="./docs/javadoc-code-snippet.jpg" alt="" width="600">


#### Swagger & Api-Docs

Used Swagger and Api-Docs for RestAPI endpoints.

`http://localhost:8081/swagger-ui/index.html?configUrl=/api-docs/swagger-config`

<img src="./docs/swagger-ui.jpg" alt="" width="600">


## Notes & Scenario

**Scenario:** Create a new customer, create a new flight and customer books this flight

...


## References

...


## License
Restaurant Service is developed under the [MIT License](LICENSE).
