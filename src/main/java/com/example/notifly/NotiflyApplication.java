package com.example.notifly;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching // Enables caching for the application
public class NotiflyApplication {

	public static void main(String[] args) {
		SpringApplication.run(NotiflyApplication.class, args);
	}

}
