package com.example.customer;

import com.example.exception.DuplicteResourceException;
import com.example.exception.RequestValidationException;
import com.example.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;


// this class perform business logic

@Service
public class CustomerService {

    private final CustomerDao customerDao;

    public CustomerService(@Qualifier("jpa") CustomerDao customerDao) {
        this.customerDao = customerDao;
    }


    public List<Customer> getAllCustomers(){
        return customerDao.selectAllCustomers();
    }


    // it  should return 404 (not found) instead of 500 (internal server error)
    public Customer getCustomer(Integer id) {
        return customerDao.selectCustomerById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                                "customer with id [%s] not found".formatted(id)
                        ));

    }

    public void addCustomer(CustomerRegistrationRequest customerRegistrationRequest){

        /// check if email exists ---> throw an exception
        String email = customerRegistrationRequest.email();
        if(customerDao.isPersonExistWithEmail(email)){
            throw new DuplicteResourceException("email already taken");
        }
        /// add
        Customer customer = new Customer(
                customerRegistrationRequest.name(),
                customerRegistrationRequest.email(),
                customerRegistrationRequest.age()
        );

        customerDao.insertCustomer(customer);
    }

    public void deleteCustomerById(Integer customerId) {
        if (!customerDao.isPersonExistWithId(customerId)) {
            throw new ResourceNotFoundException(
                    "customer with id [%s] not found".formatted(customerId)
            );
        }

        customerDao.deleteCustomerById(customerId);
    }

    public void updateCustomer(Integer customerId , CustomerUpdateRequest updateRequest) {
        Customer customer = getCustomer(customerId);

        boolean isChanged = false;

        if(updateRequest.name() != null && !updateRequest.name().equals(customer.getName()) ){
            customer.setName(updateRequest.name());
            isChanged = true;
        }

        if(updateRequest.email() != null && !updateRequest.email().equals(customer.getEmail()) ){
            if (updateRequest.email() != null && !updateRequest.email().equals(customer.getEmail())) {
                if (customerDao.isPersonExistWithEmail(updateRequest.email())) {
                    throw new DuplicteResourceException(
                            "email already taken"
                    );
                }
                customer.setEmail(updateRequest.email());
                isChanged = true;
            }
        }

        if(updateRequest.age() != null && !updateRequest.age().equals(customer.getAge()) ){
            customer.setAge(updateRequest.age());
            isChanged = true;
        }


        /// if data is the same as it is requested to be updated
        if (!isChanged) {
            throw new RequestValidationException("no data changes , as it remains the same");
        }

        customerDao.updateCustomer(customer);

    }
}
