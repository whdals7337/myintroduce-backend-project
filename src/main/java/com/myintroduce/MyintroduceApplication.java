package com.myintroduce;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
public class MyintroduceApplication {

	public static void main(String[] args) {
		SpringApplication.run(MyintroduceApplication.class, args);
	}

}
