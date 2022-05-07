package com.api.LibraryAPI;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import com.api.LibraryAPI.exceptions.ApiErrors;
import com.api.LibraryAPI.exceptions.BusinessException;

@RestControllerAdvice
public class ApplicationControllerAdvice {
	
	@ExceptionHandler(MethodArgumentNotValidException.class)//Define que para toda exception do tipo MethodArgumentNotValid será chamado esse método ApiErrors
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ApiErrors handleValidationExceptions(MethodArgumentNotValidException e) {
		BindingResult binding = e.getBindingResult();
		return new ApiErrors(binding);
	}
	
	@ExceptionHandler(BusinessException.class)//Define que para toda exception do tipo BusinessException será chamado esse método ApiErrors
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ApiErrors handleBusinessExceptions(BusinessException e) {
		return new ApiErrors(e);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@ExceptionHandler(ResponseStatusException.class)//Define que para toda exception do tipo BusinessException será chamado esse método ApiErrors
	public ResponseEntity handleResponseStatusException(ResponseStatusException ex) {
		return new ResponseEntity(new ApiErrors(ex), ex.getStatus());
	}
}
