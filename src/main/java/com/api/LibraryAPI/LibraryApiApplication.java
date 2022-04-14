package com.api.LibraryAPI;

import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class LibraryApiApplication {
	
	@Bean
	public ModelMapper modelMapper() {
		//Define um singleton do tipo modelMapper
		return new ModelMapper();
	}
	
	public static void main(String[] args) {
		SpringApplication.run(LibraryApiApplication.class, args);
	}

}
