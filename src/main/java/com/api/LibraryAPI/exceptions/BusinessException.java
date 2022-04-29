package com.api.LibraryAPI.exceptions;

@SuppressWarnings("serial")
public class BusinessException extends RuntimeException {
	
	public BusinessException(String str) {
		super(str);
	}

}
