package com.example.customer;


import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    //@GetMapping("/api/v1/customers")
    @GetMapping
    public List<Customer> getCutomers() {
        return customerService.getAllCustomers();
    }


    @GetMapping("{customerId}")
    public Customer getCustomer(@PathVariable Integer customerId) {
        return customerService.getCustomer(customerId);
    }

    //@PostMapping("/api/v1/customer")
    @PostMapping
    public void registerCustomer(@RequestBody CustomerRegistrationRequest request) {
        customerService.addCustomer(request);
    }

    @DeleteMapping("{customerId}")
    public void deleteCustomer(
            @PathVariable("customerId") Integer customerId) {
        customerService.deleteCustomerById(customerId);
    }

    @PutMapping("{customerId}")
    public void updateCustomer(
            @PathVariable("customerId") Integer customerId,
            @RequestBody CustomerUpdateRequest updateRequest) {
        customerService.updateCustomer(customerId, updateRequest);
    }
}