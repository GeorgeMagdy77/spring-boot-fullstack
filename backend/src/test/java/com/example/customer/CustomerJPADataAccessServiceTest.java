package com.example.customer;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


class CustomerJPADataAccessServiceTest {

    private CustomerJPADataAccessService underTest;
    private AutoCloseable autoCloseable;

    @Mock
    private CustomerRepository customerRepository;  // customer repository will be a mock

    @BeforeEach // Before Each Test
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this); // to initialize the mock and close the resource after each test
        underTest = new CustomerJPADataAccessService(customerRepository);
    }

    @AfterEach // After Each Test
    void tearDown() throws Exception {
        autoCloseable.close();
    }


    // we just want to verify that the method from the repository is invoked
    @Test
    void selectAllCustomers() {
        //Given
        // nothing to do because the method doesn't accept anything

        //When
        underTest.selectAllCustomers();

        //Then
        // we just want to verify that the method from the repository is invoked
        Mockito.verify(customerRepository).findAll(); // if selectAllCustomer invoked another method instead of findAll for example deleteAll then the test will fail

    }


    // we just want to verify that the value is passed correctly to the method inside the repository and the method is invoked
    @Test
    void selectCustomerById() {
        //Given
        int id = 1;

        //when
        underTest.selectCustomerById(id);

        //Then
        // we just want to verify that the value is passed correctly to the method inside the repository and the method is invoked
        Mockito.verify(customerRepository).findById(id);
    }

    @Test
    void insertCustomer() {
        //Given
        Customer customer = new Customer(1,"Ali","ali@gmail.com",2,Gender.MALE);

        //When
        underTest.insertCustomer(customer);

        //Then
        // we just want to verify that the value is passed correctly to the method inside the repository and the method is invoked
        Mockito.verify(customerRepository).save(customer);
    }

    @Test
    void isPersonExistWithEmail() {
        //Given
        String email = "foo@gmail.com";

        //When
        underTest.isPersonExistWithEmail(email);

        //Then
        // we just want to verify that the value is passed correctly to the method inside the repository and the method is invoked
        Mockito.verify(customerRepository).existsCustomerByEmail(email);
    }

    @Test
    void isPersonExistWithId() {
        int id = 1;

        //when
        underTest.isPersonExistWithId(id);

        //Then
        // we just want to verify that the value is passed correctly to the method inside the repository and the method is invoked
        Mockito.verify(customerRepository).existsCustomerById(id);
    }

    @Test
    void deleteCustomerById() {
        int id = 1;

        //when
        underTest.deleteCustomerById(id);

        //Then
        // we just want to verify that the value is passed correctly to the method inside the repository and the method is invoked
        Mockito.verify(customerRepository).deleteById(id);

    }

    @Test
    void updateCustomer() {
        //Given
        Customer customer = new Customer(1,"Ali","ali@gmail.com",2, Gender.MALE);

        //When
        underTest.updateCustomer(customer);

        //Then
        // we just want to verify that the value is passed correctly to the method inside the repository and the method is invoked
        // it will fail if we passed new customer object in CustomerJPADataAccessService as it test that the object passed (send) is the same that is receive
        Mockito.verify(customerRepository).save(customer);
    }
}