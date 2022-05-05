package com.api.LibraryAPI.api.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Optional;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import com.api.LibraryAPI.controller.LoanController;
import com.api.LibraryAPI.exceptions.BusinessException;
import com.api.LibraryAPI.models.Book;
import com.api.LibraryAPI.models.Loan;
import com.api.LibraryAPI.models.LoanDto;
import com.api.LibraryAPI.models.LoanFilterDto;
import com.api.LibraryAPI.models.ReturnedLoanDto;
import com.api.LibraryAPI.service.BookService;
import com.api.LibraryAPI.service.LoanService;
import com.api.LibraryAPI.service.LoanServiceTest;
import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@WebMvcTest(controllers = LoanController.class)
@AutoConfigureMockMvc
public class LoanControllerTest {
	
	static final String LOAN_API = "/api/loans";
	
	@Autowired
	MockMvc mvc;
	
	@MockBean
	private BookService bookService;
	
	@MockBean
	private LoanService loanService;
	
	@Test
	@DisplayName("Deve realizar um empréstimo")
	public void createLoanTest() throws Exception {
		//Cenario
		LoanDto dto = LoanDto.builder().customer("John Doe").isbn("321").build();
		String json = new ObjectMapper().writeValueAsString(dto);
		
		Book book = Book.builder().id(1l).isbn("321").build();
		BDDMockito.given( bookService.getBookByIsbn("321")).willReturn( Optional.of( book ) );
		Loan loan = Loan.builder().id(1l).customer("John Doe").book(book).date(LocalDate.now()).build();
		BDDMockito.given( loanService.save(Mockito.any(Loan.class)) ).willReturn(loan);
		
		//Execucao
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(LOAN_API)
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
				.content(json);
		
		//Verificacao
		mvc.perform(request)
			.andExpect( status().isCreated() )
			.andExpect( org.springframework.test.web.servlet.result.MockMvcResultMatchers.content().string("1") );
	}
	
	@Test
	@DisplayName("Deve retornar erro ao tentar fazer empréstimo de um isbn inexistente")
	public void invalidLoanTest() throws Exception {
		LoanDto dto = LoanDto.builder().customer("John Doe").isbn("321").build();
		String json = new ObjectMapper().writeValueAsString(dto);
		
		BDDMockito.given( bookService.getBookByIsbn("321")).willReturn( Optional.empty() );
		
		//Execucao
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(LOAN_API)
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
				.content(json);
		//Verificacao
		mvc.perform(request)
				.andExpect( status().isBadRequest() )
				.andExpect( jsonPath("errors",Matchers.hasSize(1)))
				.andExpect( jsonPath("errors[0]").value("Livro não encontrado para o isbn informado!"));
	}
	
	@Test
	@DisplayName("Deve retornar erro ao tentar fazer empréstimo de um livro emprestado")
	public void alreadyLoanedTest() throws Exception {
		//Cenario
		LoanDto dto = LoanDto.builder().customer("John Doe").isbn("321").build();
		String json = new ObjectMapper().writeValueAsString(dto);
				
		Book book = Book.builder().id(1l).isbn("321").build();
		BDDMockito.given( bookService.getBookByIsbn("321")).willReturn( Optional.of( book ) );
		BDDMockito.given( loanService.save(Mockito.any(Loan.class)) ).willThrow( new BusinessException("Livro ja está emprestado!"));
		
		//Execucao
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(LOAN_API)
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
				.content(json);
		//Verificacao
		mvc.perform(request)
				.andExpect( status().isBadRequest() )
				.andExpect( jsonPath("errors",Matchers.hasSize(1)))
				.andExpect( jsonPath("errors[0]").value("Livro ja está emprestado!"));
	}
	
	@Test
	@DisplayName("Deve retornar retornar um livro")
	public void returnBookTest() throws Exception {
		ReturnedLoanDto dto = ReturnedLoanDto.builder().returned(true).build();
		Loan loan = Loan.builder().id(1l).build();
		BDDMockito.given(loanService.getById(Mockito.anyLong()))
					.willReturn(Optional.of(loan));
		
		String json = new ObjectMapper().writeValueAsString(dto);
		
		mvc.perform(
				patch(LOAN_API.concat("/1"))
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
				.content(json))
		
				.andExpect( status().isOk());
		
		Mockito.verify(loanService, Mockito.times(1)).update(loan);
		
	}
	
	@Test
	@DisplayName("Deve retornar 404 quando tentar devolver um livro inexistente")
	public void returnInexistentBookTest() throws Exception {
		ReturnedLoanDto dto = ReturnedLoanDto.builder().returned(true).build();
		BDDMockito.given(loanService.getById(Mockito.anyLong()))
					.willReturn(Optional.empty());
		
		String json = new ObjectMapper().writeValueAsString(dto);
		
		mvc.perform(
				patch(LOAN_API.concat("/1"))
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
				.content(json))
		
				.andExpect( status().isNotFound());
	}
	
	@Test
	@DisplayName("Deve filtrar empréstimos")
	public void findLoansTest() throws Exception {
		//Cenario
		Long id = 1l;
		Loan loan = LoanServiceTest.createLoan();
		loan.setId(id);
		Book book = Book.builder().id(1l).isbn("321").build();
		loan.setBook(book);
		
		BDDMockito.given( loanService.find( Mockito.any(LoanFilterDto.class), Mockito.any(Pageable.class) ))
			.willReturn( new PageImpl<Loan>( Arrays.asList(loan), PageRequest.of(0, 10), 1) );
		
		String queryString = String.format("?isbn=%s&customer=%s&page=0&size=10", book.getIsbn(), loan.getCustomer() );
		
		//Execução (When)
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get(LOAN_API.concat(queryString))
				.accept(MediaType.APPLICATION_JSON);
		
		//Verificação
		mvc.perform(request)
			.andExpect( status().isOk() )
			.andExpect( jsonPath( "content", Matchers.hasSize(1) ))
			.andExpect( jsonPath("totalElements").value(1) )
			.andExpect( jsonPath("pageable.pageSize").value(10) )
			.andExpect( jsonPath("pageable.pageNumber").value(0) );
		
	}
}
