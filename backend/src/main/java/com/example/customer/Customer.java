package com.example.customer;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.antlr.v4.runtime.misc.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(
        name = "customer",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "customer_email_unique",
                        columnNames = "email"
                )
        }
)
public class Customer {
    @Id
    @SequenceGenerator(name = "customer_id_sequence",
                       sequenceName = "customer_id_sequence")

    @GeneratedValue(strategy = GenerationType.SEQUENCE , generator = "customer_id_sequence")
    private Integer id;


    public Customer(String name, String email, Integer age) {
        this.name = name;
        this.email = email;
        this.age = age;
    }

    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String email;
    @Column(name = "age" , nullable = false)
    private Integer age;


}

