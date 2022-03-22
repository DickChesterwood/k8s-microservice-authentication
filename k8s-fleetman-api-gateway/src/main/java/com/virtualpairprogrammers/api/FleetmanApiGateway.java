package com.virtualpairprogrammers.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableAutoConfiguration(exclude = { 
	    org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class
	    })
public class FleetmanApiGateway {
	public static void main(String[] args) {
		SpringApplication.run(FleetmanApiGateway.class, args);		
	}	
}

