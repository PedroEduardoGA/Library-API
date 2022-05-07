package com.api.LibraryAPI.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.api.LibraryAPI.exceptions.BusinessException;
import com.api.LibraryAPI.models.Book;
import com.api.LibraryAPI.models.Loan;
import com.api.LibraryAPI.models.LoanFilterDto;
import com.api.LibraryAPI.repository.LoanRepository;

@Service
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

	@Override
	public Optional<Loan> getById(Long id) {
		return repository.findById(id);
	}

	@Override
	public Loan update(Loan loan) {
		return repository.save(loan);
	}

	@Override
	public Page<Loan> find(LoanFilterDto loanFilter, Pageable pageRequest) {
		return repository.findByBookIsbnOrCustomer(loanFilter.getIsbn(), loanFilter.getCustomer(), pageRequest);
	}

	@Override
	public Page<Loan> getLoansByBook(Book book, Pageable pageable) {
		return repository.findByBook(book, pageable);
	}

	@Override
	public List<Loan> getlAllLateLoans() {
		final Integer loanDays = 4;
		LocalDate threeDaysAgo = LocalDate.now().minusDays(loanDays);
		return repository.findByLoanDateLessThanAndNotReturned(threeDaysAgo);
	}

}
