package com.api.LibraryAPI.service;

import com.api.LibraryAPI.exceptions.BusinessException;
import com.api.LibraryAPI.models.Loan;
import com.api.LibraryAPI.repository.LoanRepository;

public class LoanServiceImpl implements LoanService{
	
	private LoanRepository repository;
	
	public LoanServiceImpl(LoanRepository repository) {
		this.repository = repository;
	}
	
	@Override
	public Loan save(Loan loan) {
		if( repository.existsByBookAndNotReturned(loan.getBook()) )
			throw new BusinessException("Livro ja está emprestado!");
		
		return repository.save(loan);
	}

}
