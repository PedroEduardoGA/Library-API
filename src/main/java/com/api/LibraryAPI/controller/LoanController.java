package com.api.LibraryAPI.controller;

import java.time.LocalDate;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.api.LibraryAPI.models.Book;
import com.api.LibraryAPI.models.Loan;
import com.api.LibraryAPI.models.LoanDto;
import com.api.LibraryAPI.service.BookService;
import com.api.LibraryAPI.service.LoanService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/loans")
@RequiredArgsConstructor
public class LoanController {

	private final BookService bookService;
	private final LoanService loanService;
	
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public Long create(@RequestBody LoanDto dto) {
		Book book = bookService.getBookByIsbn(dto.getIsbn()).orElseThrow( 
				() -> new ResponseStatusException(HttpStatus.BAD_REQUEST ,"Livro não encontrado para o isbn informado!"));
		Loan entity = Loan.builder()
					.costumer(dto.getCustomer())
					.book(book)
					.date(LocalDate.now()).build();
		
		entity = loanService.save(entity);
		return entity.getId();
	}
}
