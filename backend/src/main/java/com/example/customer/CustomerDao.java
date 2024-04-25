package com.example.customer;

import java.util.List;
import java.util.Optional;

public interface CustomerDao {

    List<Customer> selectAllCustomers();
    Optional<Customer> selectCustomerById(Integer id);
    void insertCustomer(Customer customer);
    boolean isPersonExistWithEmail(String email);
    boolean isPersonExistWithId(Integer id);
    void deleteCustomerById(Integer id);
    void updateCustomer(Customer update);
    Optional<Customer> selectCustomerByEmail(String email);
}
