package com.api.LibraryAPI.exceptions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.validation.BindingResult;
import org.springframework.web.server.ResponseStatusException;

public class ApiErrors {
	private List<String> errors;
	
	public ApiErrors(BindingResult binding) {
		this.errors = new ArrayList<>();
		binding.getAllErrors().forEach( error -> this.errors.add(error.getDefaultMessage()));
	}

	public ApiErrors(BusinessException e) {
		this.errors = Arrays.asList(e.getMessage());
	}
	
	public ApiErrors(ResponseStatusException e) {
		this.errors = Arrays.asList(e.getReason());
	}

	public List<String> getErrors() {
		return errors;
	}
}
