package com.example;

import com.example.customer.Customer;
import com.example.customer.CustomerRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import com.github.javafaker.Faker;
import com.github.javafaker.Name;
import java.util.Random;


@SpringBootApplication

public class SpringBootExampleApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringBootExampleApplication.class, args);
	}

	 @Bean
	 CommandLineRunner runner(CustomerRepository customerRepository){
		 return  args -> {
			 var faker = new Faker();
			 Random random = new Random();
			 Name name = faker.name();
			 String firstName = name.firstName();
			 String lastName = name.lastName();
			 int age = random.nextInt(16, 99);
			 String email = firstName.toLowerCase() + "." + lastName.toLowerCase() + "@george.com";
			 Customer customer = new Customer(
					 firstName +  " " + lastName,
					 email,
					 age);
			 customerRepository.save(customer);
		 };
	 }
}
