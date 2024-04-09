package com.example;

import com.github.javafaker.Faker;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;

//abstract class for not instantiate but for reusing
@Testcontainers
public abstract class AbstractTestContainersUnitTest {

    @Container
    protected static final PostgreSQLContainer<?> postgreSQLContainer =
            new PostgreSQLContainer<>("postgres:latest")
                    .withDatabaseName("george-dao-unit-test")
                    .withUsername("george")
                    .withPassword("password");
    // latest version of postgres image to work with any version of postgres



    // in order to connect our application to the above postgresContainer database ,
    // we have to map the datasource (url , username ,password) to our test container ,
    // so we do this programmatically using the below code
    // so we can connect to our database from our test

    @DynamicPropertySource
    private static void registerDataSourceProperties(
            DynamicPropertyRegistry registry) {
        registry.add(
                "spring.datasource.url",
                postgreSQLContainer::getJdbcUrl
        );
        registry.add(
                "spring.datasource.username",
                postgreSQLContainer::getUsername
        );
        registry.add(
                "spring.datasource.password",
                postgreSQLContainer::getPassword
        );
    }

   // Using this faker object we will access other available methods that can be used to generate random , effective and fake data.

    protected static final Faker FAKER = new Faker();


}
