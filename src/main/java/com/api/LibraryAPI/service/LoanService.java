package com.api.LibraryAPI.service;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.api.LibraryAPI.models.Loan;
import com.api.LibraryAPI.models.LoanFilterDto;

public interface LoanService {
	
	Loan save(Loan loan);

	Optional<Loan> getById(Long id);

	Loan update(Loan loan);

	Page<Loan> find(LoanFilterDto any, Pageable any2);
}
