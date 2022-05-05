package com.api.LibraryAPI.service;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.api.LibraryAPI.models.Book;
import com.api.LibraryAPI.models.Loan;
import com.api.LibraryAPI.models.LoanFilterDto;

public interface LoanService {
	
	Loan save(Loan loan);

	Optional<Loan> getById(Long id);

	Loan update(Loan loan);

	Page<Loan> find(LoanFilterDto loanFilter, Pageable pageRequest);

	Page<Loan> getLoansByBook(Book book, Pageable pageable);
	
	List<Loan> getlAllLateLoans();
}
