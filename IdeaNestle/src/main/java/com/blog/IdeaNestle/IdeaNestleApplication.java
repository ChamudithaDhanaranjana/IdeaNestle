package com.blog.IdeaNestle;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.time.format.DateTimeFormatter;

@SpringBootApplication
public class IdeaNestleApplication {

	public static void main(String[] args) {
		SpringApplication.run(IdeaNestleApplication.class, args);
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/YYYY");
	}
}
