package com.api.LibraryAPI.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.api.LibraryAPI.exceptions.BusinessException;
import com.api.LibraryAPI.models.Book;
import com.api.LibraryAPI.models.Loan;
import com.api.LibraryAPI.models.LoanFilterDto;
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
		Loan savingLoan = createLoan();
		Book book = createLoan().getBook();
		Loan savedLoan = Loan.builder().id(1l).customer("John Doe").book(book).date(LocalDate.now()).build();
		when( repository.existsByBookAndNotReturned(book) ).thenReturn(false);
		when( repository.save(savingLoan)).thenReturn(savedLoan);
		
		//Execucao
		Loan loan = loanService.save(savingLoan);
		
		//Verificacoes
		assertThat(loan.getId()).isEqualTo(savedLoan.getId());
		assertThat(loan.getCustomer()).isEqualTo(savedLoan.getCustomer());
		assertThat(loan.getDate()).isEqualTo(savedLoan.getDate());
		assertThat(loan.getBook()).isEqualTo(savedLoan.getBook());
		
		
	}
	
	@Test
	@DisplayName("Deve salvar lançar business exception ao tentar salvar livro já emprestado")
	public void trySaveLoanedBookTest() {
		//Cenario
		Book book = Book.builder().id(1l).build();
		Loan savingLoan = createLoan();
		when( repository.existsByBookAndNotReturned(book) ).thenReturn(true);
		
		//Execucao
		Throwable ex = catchThrowable( () -> loanService.save(savingLoan));
		
		//Verificacoes
		assertThat(ex)
			.isInstanceOf(BusinessException.class)
			.hasMessage("Livro ja está emprestado!");
		
		verify(repository , never()).save(savingLoan);
	}
	
	@Test
	@DisplayName("Deve obter informações de um empréstimo pelo ID")
	public void getLoanDetailsTest() {
		//Cenario
		Long id = 1l;
		Loan loan = createLoan();
		loan.setId(id);
		
		Mockito.when(repository.findById(id)).thenReturn(Optional.of(loan));
		
		//Execucao
		Optional<Loan> result = loanService.getById(id);
		
		assertThat(result.isPresent()).isTrue();
		assertThat(result.get().getId()).isEqualTo(id);
		assertThat(result.get().getCustomer()).isEqualTo(loan.getCustomer());
		assertThat(result.get().getBook()).isEqualTo(loan.getBook());
		assertThat(result.get().getDate()).isEqualTo(loan.getDate());
		
		Mockito.verify(repository).findById(id);
	}
	
	@Test
	@DisplayName("Deve obter informações de um empréstimo pelo ID")
	public void updateLoanTest() {
		//Cenario
		Loan loan = createLoan();
		loan.setId(1l);
		loan.setReturned(true);
		
		when( repository.save(loan) ).thenReturn(loan);
		Loan updated = loanService.update(loan);
		
		assertThat(updated.getReturned()).isTrue();
		Mockito.verify(repository).save(loan);
	}
	
	public static Loan createLoan() {
		Book book = Book.builder().id(1l).build();
		return Loan.builder().customer("John Doe").book(book).date(LocalDate.now()).build();
	}
	
	@SuppressWarnings("unchecked")
	@Test
	@DisplayName("Deve filtrar empréstimos pelas propriedades")
	public void findLoanTest() throws Exception {
		//Cenario
		LoanFilterDto loanFilter = LoanFilterDto.builder().customer("John Doe").isbn("321").build();
		Long id = 1l;
		Loan loan = createLoan();
		loan.setId(id);
		
		PageRequest pageRequest =  PageRequest.of(0, 10);
		List<Loan> list = Arrays.asList(loan);
		Page<Loan> page = new PageImpl<Loan>(list, pageRequest, list.size());
		
		Mockito.when( repository.findByBookIsbnOrCustomer( 
					Mockito.anyString(), 
					Mockito.anyString(),
					Mockito.any(PageRequest.class )))
			.thenReturn(page);
		
		//Execucao
		Page<Loan> result = loanService.find(loanFilter, pageRequest);
		
		//Verificacao
		assertThat( result.getTotalElements()).isEqualTo(1);
		assertThat(result.getContent()).isEqualTo(list);
		assertThat(result.getPageable().getPageNumber()).isEqualTo(0);
		assertThat(result.getPageable().getPageSize()).isEqualTo(10);
		
	}
}
