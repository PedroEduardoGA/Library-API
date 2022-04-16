package com.api.LibraryAPI.controller;

import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import com.api.LibraryAPI.exceptions.ApiErrors;
import com.api.LibraryAPI.exceptions.BusinessException;
import com.api.LibraryAPI.models.Book;
import com.api.LibraryAPI.models.BookDto;
import com.api.LibraryAPI.service.BookService;

@RestController
@RequestMapping("/api/books")
public class BookController {
	
	
	private BookService bookService;
	private ModelMapper modelMapper;
	
	public BookController(BookService bookService, ModelMapper modelMapper) {
		this.bookService = bookService;
		this.modelMapper = modelMapper;
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public BookDto createBook(@RequestBody BookDto bookDto) {//@Valid
		Book entity = modelMapper.map(bookDto, Book.class);
		entity = bookService.save(entity);
		
		return modelMapper.map(entity, BookDto.class);
	}
	
	@ExceptionHandler(MethodArgumentNotValidException.class)	//Define que para toda exception do tipo MethodArgumentNotValid será chamado esse método ApiErrors
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ApiErrors handleValidationExceptions(MethodArgumentNotValidException e) {
		BindingResult binding = e.getBindingResult();
		return new ApiErrors(binding);
	}
	
	@ExceptionHandler(BusinessException.class)	//Define que para toda exception do tipo MethodArgumentNotValid será chamado esse método ApiErrors
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ApiErrors handleBusinessExceptions(BusinessException e) {
		return new ApiErrors(e);
	}
}
