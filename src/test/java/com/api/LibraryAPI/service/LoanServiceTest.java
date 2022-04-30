package com.api.LibraryAPI.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.api.LibraryAPI.exceptions.BusinessException;
import com.api.LibraryAPI.models.Book;
import com.api.LibraryAPI.models.Loan;
import com.api.LibraryAPI.repository.LoanRepository;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class LoanServiceTest {
	
	LoanService loanService;
	
	@MockBean
	LoanRepository repository;
	
	@BeforeEach
	public void SetUp() {
		this.loanService = new LoanServiceImpl(repository);
	}
	
	@Test
	@DisplayName("Deve salvar um empréstimo")
	public void saveLoanTest() {
		//Cenario
		Book book = Book.builder().id(1l).build();
		Loan savingLoan = Loan.builder().costumer("John Doe").book(book).date(LocalDate.now()).build();
		Loan savedLoan = Loan.builder().id(1l).costumer("John Doe").book(book).date(LocalDate.now()).build();
		when( repository.existsByBookAndNotReturned(book) ).thenReturn(false);
		when( repository.save(savingLoan)).thenReturn(savedLoan);
		
		//Execucao
		Loan loan = loanService.save(savingLoan);
		
		//Verificacoes
		assertThat(loan.getId()).isEqualTo(savedLoan.getId());
		assertThat(loan.getCostumer()).isEqualTo(savedLoan.getCostumer());
		assertThat(loan.getDate()).isEqualTo(savedLoan.getDate());
		assertThat(loan.getBook()).isEqualTo(savedLoan.getBook());
		
		
	}
	
	@Test
	@DisplayName("Deve salvar lançar business exception ao tentar salvar livro já emprestado")
	public void trySaveLoanedBookTest() {
		//Cenario
		Book book = Book.builder().id(1l).build();
		Loan savingLoan = Loan.builder().costumer("John Doe").book(book).date(LocalDate.now()).build();
		when( repository.existsByBookAndNotReturned(book) ).thenReturn(true);
		
		//Execucao
		Throwable ex = catchThrowable( () -> loanService.save(savingLoan));
		
		//Verificacoes
		assertThat(ex)
			.isInstanceOf(BusinessException.class)
			.hasMessage("Livro ja está emprestado!");
		
		verify(repository , never()).save(savingLoan);
	}
}
