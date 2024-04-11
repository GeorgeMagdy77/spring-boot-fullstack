package com.example;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


// This class is for us to test that Continuous Delivery is working fine


@RestController
public class PingPongController {

    record PingPong(String result){}

    @GetMapping("/ping")
    public PingPong getPingPong() {
        return new PingPong("Pong");
    }
}