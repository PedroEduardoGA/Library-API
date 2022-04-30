package com.api.LibraryAPI.api.controller;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import java.time.LocalDate;
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
import com.api.LibraryAPI.service.BookService;
import com.api.LibraryAPI.service.LoanService;
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
		Loan loan = Loan.builder().id(1l).costumer("John Doe").book(book).date(LocalDate.now()).build();
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
}
