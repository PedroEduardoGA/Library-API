package com.api.LibraryAPI.models;

import javax.validation.constraints.NotEmpty;
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
	
	@NotEmpty
	private String customer;
	
	@NotEmpty
	private String isbn;
	
	@NotEmpty
	private String email;
	private BookDto book;
}
