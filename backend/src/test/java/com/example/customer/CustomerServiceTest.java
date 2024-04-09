package com.example.customer;

import com.example.exception.DuplicteResourceException;
import com.example.exception.RequestValidationException;
import com.example.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class) // this line instead of line 23 in order to avoid writing boilerplate code ( another way of initializing mock)
class CustomerServiceTest {

    private CustomerService underTest ;
    private AutoCloseable autoCloseable;

    @Mock
    CustomerDao customerDao; // customer repository will be a mock


    @BeforeEach
    void setUp() {
         //autoCloseable = MockitoAnnotations.openMocks(this); // to initialize the mock and close the resource after each test
        underTest = new CustomerService(customerDao);
    }


    @Test
    void getAllCustomers() {
        //When
        underTest.getAllCustomers();

        //Then
        // we just want to verify that the method from the DAO is invoked
        verify(customerDao).selectAllCustomers();
    }

    @Test
    void canGetCustomer() {
        //Given
        int id = 10;
        Customer customer = new Customer(id , "George" , "george@gmail.com" , 22);

        // tell the Mock customerDao what to do when calling selectCustomerById please return Optional customer whether it is empty or George as it doesn't know to how reply
        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

        //When
        Customer actual = underTest.getCustomer(id);

        //Then
        // we just want to verify that the method from the DAO is invoked and can return the Specific customer
        assertThat(actual).isEqualTo(customer);
    }

    @Test
    void willThrowGetCustomerReturnEmptyOptional() {
        //Given
        int id = 10;

        // tell the Mock customerDao what to do when calling selectCustomerById please return Optional empty customer as it doesn't know to how reply then it will throw an exception
        when(customerDao.selectCustomerById(id)).thenReturn(Optional.empty());

        //When
        //Then
        // we just want to verify that the method throw ResourceNotFound exception with message "customer with id [%s] not found"
        assertThatThrownBy(() -> underTest.getCustomer(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("customer with id [%s] not found".formatted(id));
    }


    @Test
    void addCustomer() {
        //Given
        String email = "george@gmail.com";

        // tell the Mock customerDao what to do when calling isPersonExistWithEmail please return false when given email with value "george@gmail.com" as it doesn't know to how reply
        when(customerDao.isPersonExistWithEmail(email)).thenReturn(false);

        CustomerRegistrationRequest request = new CustomerRegistrationRequest("george" ,email , 22);

        //When
        underTest.addCustomer(request);

        //Then
        ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);

        // we just want to verify that the method from the DAO is invoked , and we want to extract the value of the customer that is passed to perform assertions
        verify(customerDao).insertCustomer(customerArgumentCaptor.capture());

        // customer that is extracted
        Customer captauredCustomer = customerArgumentCaptor.getValue();

        // assert ID is NUll as it will be generated By the Database
        assertThat(captauredCustomer.getId()).isNull();
        //to make sure that the customer that is created in CustomerService and sent to our DAO is the customer that will be sent with all it's value (Never Changes)
        // we compare the values against what was sent
        assertThat(captauredCustomer.getName()).isEqualTo(request.name());
        assertThat(captauredCustomer.getEmail()).isEqualTo(request.email());
        assertThat(captauredCustomer.getAge()).isEqualTo(request.age());

    }


    @Test
    void willThrowWhenEmailExistWhileAddingCustomer() {
        //Given
        String email = "george@gmail.com";

        // tell the Mock customerDao what to do when calling isPersonExistWithEmail please return true when given email with value "george@gmail.com" as it doesn't know to how reply
        when(customerDao.isPersonExistWithEmail(email)).thenReturn(true);

        CustomerRegistrationRequest request = new CustomerRegistrationRequest("george" ,email , 22);

        //When
        assertThatThrownBy(() -> underTest.addCustomer(request))
                .isInstanceOf(DuplicteResourceException.class)
                .hasMessage("email already taken");

        //Then
        // we just want to verify that we will never insert a customer and insert customer is never invoked as the exception was thrown
        verify(customerDao , never() ).insertCustomer(any());

    }



    @Test
    void deleteCustomerById() {
        //Given
        int id = 10;

        when(customerDao.isPersonExistWithId(id)).thenReturn(true);

        //When
        underTest.deleteCustomerById(id);

        //Then
        // we just want to verify that the method from the DAO is invoked
        verify(customerDao).deleteCustomerById(id);
    }

    @Test
    void willThrowWhenDeleteCustomerByIdNotExist() {
        //Given
        int id = 10;

        when(customerDao.isPersonExistWithId(id)).thenReturn(false);

        //When
        assertThatThrownBy(() -> underTest.deleteCustomerById(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("customer with id [%s] not found".formatted(id));

        //Then
        // we just want to verify that we will never delete a customer and delete customer is never invoked as the exception was thrown
        verify(customerDao , never() ).deleteCustomerById(id);
    }


    @Test
    void canUpdateAllCustomersProperties() {
        //Given
        int id = 10;
        Customer customer = new Customer(id , "George" , "george@gmail.com" , 22);

        // tell the Mock customerDao what to do when calling selectCustomerById please return Optional customer whether it is empty or George as it doesn't know to how reply
        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

        String newEmail = "daniel@gmail.com";

        CustomerUpdateRequest updateRequest = new CustomerUpdateRequest("Daniel" , newEmail , 20);

        // tell the Mock customerDao what to do when calling isPersonExistWithEmail please return false as it doesn't know to how reply
        when(customerDao.isPersonExistWithEmail(newEmail)).thenReturn(false);

        //When
        underTest.updateCustomer(id , updateRequest);

        //Then
        ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);

        // we just want to verify that the method from the DAO is invoked , and we want to extract the value of the customer that is passed to perform assertions
        verify(customerDao).updateCustomer(customerArgumentCaptor.capture());

        // customer that is extracted
        Customer captauredCustomer = customerArgumentCaptor.getValue();

        //to make sure that the customer that is created in CustomerService and sent to our DAO is the customer that will be sent with all it's value (Changes)
        // we compare the values against what was sent
        assertThat(captauredCustomer.getName()).isEqualTo(updateRequest.name());
        assertThat(captauredCustomer.getEmail()).isEqualTo(updateRequest.email());
        assertThat(captauredCustomer.getAge()).isEqualTo(updateRequest.age());
    }


    @Test
    void canUpdateOnlyCustomerName() {
        //Given
        int id = 10;
        Customer customer = new Customer(id , "George" , "george@gmail.com" , 22);

        // tell the Mock customerDao what to do when calling selectCustomerById please return Optional customer whether it is empty or George as it doesn't know to how reply
        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

        CustomerUpdateRequest updateRequest = new CustomerUpdateRequest("Daniel" , null , null);

        //When
        underTest.updateCustomer(id , updateRequest);

        //Then
        ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);

        // we just want to verify that the method from the DAO is invoked , and we want to extract the value of the customer that is passed to perform assertions
        verify(customerDao).updateCustomer(customerArgumentCaptor.capture());

        // customer that is extracted
        Customer captauredCustomer = customerArgumentCaptor.getValue();

        //to make sure that the customer that is created in CustomerService and sent to our DAO is the customer that will be sent with all it's value (Changes)
        // we compare the values against what was sent
        assertThat(captauredCustomer.getName()).isEqualTo(updateRequest.name());
        assertThat(captauredCustomer.getEmail()).isEqualTo(customer.getEmail());
        assertThat(captauredCustomer.getAge()).isEqualTo(customer.getAge());
    }

    @Test
    void canUpdateOnlyCustomerEmail() {
        //Given
        int id = 10;
        Customer customer = new Customer(id , "George" , "george@gmail.com" , 22);

        // tell the Mock customerDao what to do when calling selectCustomerById please return Optional customer whether it is empty or George as it doesn't know to how reply
        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

        String newEmail = "daniel@gmail.com";

        CustomerUpdateRequest updateRequest = new CustomerUpdateRequest(null , newEmail , null);

        // tell the Mock customerDao what to do when calling isPersonExistWithEmail please return false as it doesn't know to how reply
        when(customerDao.isPersonExistWithEmail(newEmail)).thenReturn(false);

        //When
        underTest.updateCustomer(id , updateRequest);

        //Then
        ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);

        // we just want to verify that the method from the DAO is invoked , and we want to extract the value of the customer that is passed to perform assertions
        verify(customerDao).updateCustomer(customerArgumentCaptor.capture());

        // customer that is extracted
        Customer captauredCustomer = customerArgumentCaptor.getValue();

        //to make sure that the customer that is created in CustomerService and sent to our DAO is the customer that will be sent with all it's value (Changes)
        // we compare the values against what was sent
        assertThat(captauredCustomer.getName()).isEqualTo(customer.getName());
        assertThat(captauredCustomer.getEmail()).isEqualTo(newEmail);
        assertThat(captauredCustomer.getAge()).isEqualTo(customer.getAge());
    }

    @Test
    void canUpdateOnlyCustomerAge() {
        //Given
        int id = 10;
        Customer customer = new Customer(id , "George" , "george@gmail.com" , 22);

        // tell the Mock customerDao what to do when calling selectCustomerById please return Optional customer whether it is empty or George as it doesn't know to how reply
        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

        CustomerUpdateRequest updateRequest = new CustomerUpdateRequest( null , null , 40);

        //When
        underTest.updateCustomer(id , updateRequest);

        //Then
        ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);

        // we just want to verify that the method from the DAO is invoked , and we want to extract the value of the customer that is passed to perform assertions
        verify(customerDao).updateCustomer(customerArgumentCaptor.capture());

        // customer that is extracted
        Customer captauredCustomer = customerArgumentCaptor.getValue();

        //to make sure that the customer that is created in CustomerService and sent to our DAO is the customer that will be sent with all it's value (Changes)
        // we compare the values against what was sent
        assertThat(captauredCustomer.getName()).isEqualTo(customer.getName());
        assertThat(captauredCustomer.getEmail()).isEqualTo(customer.getEmail());
        assertThat(captauredCustomer.getAge()).isEqualTo(updateRequest.age());
    }

    @Test
    void willThrowWhenTryingToUpdateCustomerEmailWhenAlreadyTaken() {
        //Given
        int id = 10;
        Customer customer = new Customer(id , "George" , "george@gmail.com" , 22);

        // tell the Mock customerDao what to do when calling selectCustomerById please return Optional customer whether it is empty or George as it doesn't know to how reply
        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

        String newEmail = "daniel@gmail.com";

        CustomerUpdateRequest updateRequest = new CustomerUpdateRequest(null , newEmail , null);

        // tell the Mock customerDao what to do when calling isPersonExistWithEmail please return false as it doesn't know to how reply
        when(customerDao.isPersonExistWithEmail(newEmail)).thenReturn(true);

        //When
        assertThatThrownBy(() -> underTest.updateCustomer(id , updateRequest))
                .isInstanceOf(DuplicteResourceException.class)
                .hasMessage("email already taken");

        //Then
        ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);

        // we just want to verify that we will never update customer and update customer is never invoked as the exception was thrown
        verify(customerDao , never() ).updateCustomer(any());
    }

    @Test
    void willThrowWhenCustomerHasNoChanges() {
        //Given
        int id = 10;
        Customer customer = new Customer(id , "George" , "george@gmail.com" , 22);

        // tell the Mock customerDao what to do when calling selectCustomerById please return Optional customer whether it is empty or George as it doesn't know to how reply
        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

        String newEmail = "daniel@gmail.com";

        CustomerUpdateRequest updateRequest = new CustomerUpdateRequest(customer.getName() , customer.getEmail() , customer.getAge());

        //When
        assertThatThrownBy(() ->  underTest.updateCustomer(id , updateRequest))
                     .isInstanceOf(RequestValidationException.class)
                     .hasMessage("no data changes , as it remains the same");

        //Then
        // we just want to verify that we will never update customer and update customer is never invoked as the exception was thrown
        verify(customerDao , never() ).updateCustomer(any());
    }


    /// When Make Test for the Whole Class With Coverage The CustomerService will greens all round which means it is fully tested with all it's method


}