package com.api.LibraryAPI.models;

import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookDto {

	private Long id;
	
	//@NotEmpty
	private String title;
	
	//@NotEmpty
	private String author;
	
	//@NotEmpty
	private String isbn;
	
}
