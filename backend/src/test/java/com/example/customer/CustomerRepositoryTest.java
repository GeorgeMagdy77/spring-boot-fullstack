package com.example.customer;

import com.example.AbstractTestContainersUnitTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.ApplicationContext;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

// we shouldn't use @SpringBootTest bec it will load the application context with a bunch of beans which will make the test slower
// extends the AbstractTestContainersUnitTest in order to use the database of test containers
// instead of database of docker compose
@DataJpaTest // it is only loading the beans that JPA component need to use for running testing instead of all beans inside the application context
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) // to disable the embedded database and connect to the database of the docker compose
class CustomerRepositoryTest extends AbstractTestContainersUnitTest{

    @Autowired
    private CustomerRepository underTest;



    @Autowired
    private ApplicationContext applicationContext;

    @BeforeEach
    void setUp() {
        System.out.println("No of beans loaded " + applicationContext.getBeanDefinitionCount()); // no of beans loaded which is needed to for running testing
    }

    @Test
    void existsCustomerByEmail() {

        //Given
        String email = FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();
        Customer customer = new Customer(
                FAKER.name().fullName(),
                email,
                20);

        underTest.save(customer);

        // When
        var actual = underTest.existsCustomerByEmail(email);

        // Then
        assertThat(actual).isTrue();
    }


    @Test
    void existsCustomerByEmailFailsWhenEmailNotPresent() {

        //Given
        String email = FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();


        // When
        var actual = underTest.existsCustomerByEmail(email);

        // Then
        assertThat(actual).isFalse();
    }

    @Test
    void existsCustomerById() {

        //Given
        String email = FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();
        Customer customer = new Customer(
                FAKER.name().fullName(),
                email,
                 20);

        underTest.save(customer);

        int id = underTest.findAll()
                .stream()
                .filter(c -> c.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();

        // When
        var actual = underTest.existsCustomerById(id);

        // Then
        assertThat(actual).isTrue();

    }


    @Test
    void existsCustomerByIdFailsWhenIdNotPresent() {

        //Given
        int id = -1;

        // When
        var actual = underTest.existsCustomerById(id);

        // Then
        assertThat(actual).isFalse();

    }
}