package com.example.journey;


import com.example.customer.Customer;
import com.example.customer.CustomerRegistrationRequest;
import com.example.customer.CustomerUpdateRequest;
import com.github.javafaker.Faker;
import com.github.javafaker.Name;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Random;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.data.jpa.domain.AbstractPersistable_.id;


///-------------------This file tests Multiple EndPoints for Customer, So That it is named Customer Journey (CRUD)-----------------///

@SpringBootTest(webEnvironment = RANDOM_PORT) // This annotation is needed for integration test , we want to make sure that this is as close to production as we can
                                             // (Deploy Backend API to real users)
public class CustomerIntegrationTest {

    @Autowired
    private WebTestClient webTestClient; /// this will be our postman (Web Client) to send Http requests to our server , so no need to use postman anymore


    private static final Random RANDOM = new Random();

    private static final String CUSTOMER_URI = "/api/v1/customers";

      // We can use constructor instead of autowire
//    public CustomerIntegrationTest(WebTestClient webTestClient) {
//        this.webTestClient = webTestClient;
//    }

    @Test
    void canRegisterCustomer() {
        // create registration request
        Faker faker = new Faker();
        Name fakerName = faker.name();

        String name = fakerName.fullName();
        String email = fakerName.lastName() + "-" + UUID.randomUUID() + "@gmail.com";
        int age = RANDOM.nextInt(1, 100);

        CustomerRegistrationRequest request = new CustomerRegistrationRequest(
                name, email, age
        );

        // send a post request instead of postman
        webTestClient.post()
                .uri(CUSTOMER_URI)
                .accept(MediaType.APPLICATION_JSON) // that was the server accepts
                .contentType(MediaType.APPLICATION_JSON) // that was the client sends
                .body(Mono.just(request), CustomerRegistrationRequest.class)
                .exchange()
                .expectStatus()
                .isOk();


        // get all customers
        List<Customer> allCustomers = webTestClient.get()
                .uri(CUSTOMER_URI)
                .accept(MediaType.APPLICATION_JSON) // that was the server accepts
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(new ParameterizedTypeReference<Customer>() {
                })
                .returnResult()
                .getResponseBody();

        // make sure that customer is present
        Customer expectedCustomer = new Customer(name , email , age);

        assertThat(allCustomers).usingRecursiveFieldByFieldElementComparatorIgnoringFields("id")
                .contains(expectedCustomer);

        // get customer by id from our API

        assert allCustomers != null;
        int id = allCustomers.stream()
                .filter(customer -> customer.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();

        expectedCustomer.setId(id);

        // get customer by id
        webTestClient.get()
                .uri(CUSTOMER_URI + "/{id}", id)
                .accept(MediaType.APPLICATION_JSON) // that was the server accepts
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(new ParameterizedTypeReference<Customer>() {
                })
                .isEqualTo(expectedCustomer);
    }

    @Test
    void canDeleteCustomer() {
        // create registration request
        Faker faker = new Faker();
        Name fakerName = faker.name();

        String name = fakerName.fullName();
        String email = fakerName.lastName() + "-" + UUID.randomUUID() + "@gmail.com";
        int age = RANDOM.nextInt(1, 100);

        CustomerRegistrationRequest request = new CustomerRegistrationRequest(
                name, email, age
        );

        // send a post request
        webTestClient.post()
                .uri(CUSTOMER_URI)
                .accept(MediaType.APPLICATION_JSON) // that was the server accepts
                .contentType(MediaType.APPLICATION_JSON) // that was the client sends
                .body(Mono.just(request), CustomerRegistrationRequest.class)
                .exchange()
                .expectStatus()
                .isOk();

        // get all customers
        List<Customer> allCustomers = webTestClient.get()
                .uri(CUSTOMER_URI)
                .accept(MediaType.APPLICATION_JSON) // that was the server accepts
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(new ParameterizedTypeReference<Customer>() {
                })
                .returnResult()
                .getResponseBody();


        assert allCustomers != null;
        int id = allCustomers.stream()
                .filter(customer -> customer.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();

        // delete customer

        webTestClient.delete()
                .uri(CUSTOMER_URI + "/{id}", id)
                .accept(MediaType.APPLICATION_JSON) // that was the server accepts
                .exchange()
                .expectStatus()
                .isOk();

        // get customer by id
        webTestClient.get()
                .uri(CUSTOMER_URI + "/{id}", id)
                .accept(MediaType.APPLICATION_JSON) // that was the server accepts
                .exchange()
                .expectStatus()
                .isNotFound();
    }

    @Test
    void canUpdateCustomer() {
        // create registration request
        Faker faker = new Faker();
        Name fakerName = faker.name();

        String name = fakerName.fullName();
        String email = fakerName.lastName() + "-" + UUID.randomUUID() + "@gmail.com";
        int age = RANDOM.nextInt(1, 100);

        CustomerRegistrationRequest request = new CustomerRegistrationRequest(
                name, email, age
        );

        // send a post request
        webTestClient.post()
                .uri(CUSTOMER_URI)
                .accept(MediaType.APPLICATION_JSON) // that was the server accepts
                .contentType(MediaType.APPLICATION_JSON) // that was the client sends
                .body(Mono.just(request), CustomerRegistrationRequest.class)
                .exchange()
                .expectStatus()
                .isOk();

        // get all customers
        List<Customer> allCustomers = webTestClient.get()
                .uri(CUSTOMER_URI)
                .accept(MediaType.APPLICATION_JSON) // that was the server accepts
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(new ParameterizedTypeReference<Customer>() {
                })
                .returnResult()
                .getResponseBody();


        assert allCustomers != null;
        int id = allCustomers.stream()
                .filter(customer -> customer.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();

        // update customer

        String newName = "Messi";

        CustomerUpdateRequest updateRequest = new CustomerUpdateRequest(
                newName, null, null
        );

        webTestClient.put()
                .uri(CUSTOMER_URI + "/{id}", id)
                .accept(MediaType.APPLICATION_JSON) // that was the server accepts
                .contentType(MediaType.APPLICATION_JSON) // that was the client sends
                .body(Mono.just(updateRequest), CustomerUpdateRequest.class)
                .exchange()
                .expectStatus()
                .isOk();

        // get customer by id
        Customer updatedCustomer = webTestClient.get()
                .uri(CUSTOMER_URI + "/{id}", id)
                .accept(MediaType.APPLICATION_JSON) // that was the server accepts
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(Customer.class)
                .returnResult()
                .getResponseBody();

        Customer expectedCustomer = new Customer(
                id, newName, email, age
        );

        assertThat(updatedCustomer).isEqualTo(expectedCustomer);
    }

}
