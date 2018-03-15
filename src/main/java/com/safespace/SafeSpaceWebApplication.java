package com.safespace;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("com.safespace")
public class SafeSpaceWebApplication {

	public static void main(String[] args) throws Exception {
		SpringApplication.run(SafeSpaceWebApplication.class, args);
	}

}