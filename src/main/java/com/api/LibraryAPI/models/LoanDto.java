package com.api.LibraryAPI.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoanDto {
	
	private long id;
	private String customer;
	private String isbn;
	private BookDto book;
}
