package org.uta.datamining.spring2019;

import java.io.FileNotFoundException;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) throws FileNotFoundException {
		SpringApplication.run(DemoApplication.class, args);
	}

}

