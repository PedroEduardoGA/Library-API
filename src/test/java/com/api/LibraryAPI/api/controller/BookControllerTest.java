package com.api.LibraryAPI.api.controller;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
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
import com.api.LibraryAPI.exceptions.BusinessException;
import com.api.LibraryAPI.models.Book;
import com.api.LibraryAPI.models.BookDto;
import com.api.LibraryAPI.service.BookService;
import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@WebMvcTest
@AutoConfigureMockMvc
public class BookControllerTest {

	static String BOOK_API = "/api/books";
	
	@Autowired
	MockMvc mvc;
	
	@MockBean
	BookService bookService;
	
	@Test
	@DisplayName("Deve criar um livro com sucesso")
	public void createBookTest() throws Exception {
		
		BookDto book = BookDto.builder().title("O garoto estudioso").author("Escritor").isbn("1690401").build();
		Book savedBook = Book.builder().id(Long.valueOf(10)).title("O garoto estudioso").author("Escritor").isbn("1690401").build();
		
		BDDMockito.given(bookService.save(Mockito.any(Book.class))).willReturn(savedBook);
		String json = new ObjectMapper().writeValueAsString(book);
		
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(BOOK_API)
		.contentType(MediaType.APPLICATION_JSON)
		.accept(MediaType.APPLICATION_JSON)
		.content(json);
		
		mvc.perform(request)
		.andExpect(status().isCreated())
		.andExpect(jsonPath("id").isNotEmpty())
		.andExpect(jsonPath("title").value(book.getTitle()))
		.andExpect(jsonPath("author").value(book.getAuthor()))
		.andExpect(jsonPath("isbn").value(book.getIsbn()));
	}
	
	@Test
	@DisplayName("Deve lançar erro de validação por falta de dados na criação do livro")
	public void createInvalidBookTest() throws Exception {
		
		String json = new ObjectMapper().writeValueAsString(new BookDto());
		
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(BOOK_API)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.content(json);
		
		mvc.perform(request)
		.andExpect( status().isBadRequest() )
		.andExpect( jsonPath( "errors", Matchers.hasSize(3)));
	}
	
	@Test
	@DisplayName("Deve lançar erro ao tentar criar livro com isbn repetido")
	public void createBookInvalidIsbn() throws Exception {
		
		BookDto book = BookDto.builder().title("O garoto estudioso").author("Escritor").isbn("1690401").build();
		String json = new ObjectMapper().writeValueAsString(book);
		BDDMockito.given(bookService.save(Mockito.any(Book.class))).willThrow(new BusinessException("Isbn ja cadastrado"));
		
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(BOOK_API)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.content(json);
		
		mvc.perform(request)
		.andExpect( status().isBadRequest() )
		.andExpect( jsonPath( "errors", Matchers.hasSize(1)))
		.andExpect( jsonPath( "errors[0]").value("Isbn ja cadastrado"));
	
	}
}
