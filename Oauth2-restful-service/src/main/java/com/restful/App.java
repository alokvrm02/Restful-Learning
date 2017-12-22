package com.restful;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jdbc.core.JdbcTemplate;

@SpringBootApplication
public class App {

	
	 @Autowired
	    JdbcTemplate jdbcTemplate;
	 
	public static void main(String[] args) {
		SpringApplication.run(App.class, args);
	}
}
