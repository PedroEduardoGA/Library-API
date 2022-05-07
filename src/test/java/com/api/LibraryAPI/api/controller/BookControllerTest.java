package com.api.LibraryAPI.api.controller;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
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

import com.api.LibraryAPI.controller.BookController;
import com.api.LibraryAPI.exceptions.BusinessException;
import com.api.LibraryAPI.models.Book;
import com.api.LibraryAPI.models.BookDto;
import com.api.LibraryAPI.service.BookService;
import com.api.LibraryAPI.service.LoanService;
import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@WebMvcTest(controllers = BookController.class)
@AutoConfigureMockMvc
public class BookControllerTest {

	static String BOOK_API = "/api/books";
	
	@Autowired
	MockMvc mvc;
	
	@MockBean
	BookService bookService;
	
	@MockBean
	private LoanService loanService;
	
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
	
	@Test
	@DisplayName("Deve obter os detalhes de um livro salvo no banco de dados")
	public void getBookDetails() throws Exception{
		//Cenário (Given)
		Long id = 1l;
		Book book = Book.builder().id(id).title("O garoto estudioso").author("Escritor").isbn("1690401").build();
		
		BDDMockito.given( bookService.getById(id)).willReturn(Optional.of(book));
		
		//Execução (When)
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get(BOOK_API.concat("/"+id)).accept(MediaType.APPLICATION_JSON);
		
		//Verificação
		mvc.perform(request)
			.andExpect(status().isOk())
			.andExpect(jsonPath("id").value(id))
			.andExpect(jsonPath("title").value(book.getTitle()))
			.andExpect(jsonPath("author").value(book.getAuthor()))
			.andExpect(jsonPath("isbn").value(book.getIsbn()));
	}	
	
	@Test
	@DisplayName("Deve retornar Not_Found quando livro não está no banco de dados")
	public void bookNotFound() throws Exception{
		//Cenário (Given)
		BDDMockito.given( bookService.getById( Mockito.anyLong() )).willReturn(Optional.empty());
		
		//Execução (When)
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get(BOOK_API.concat("/"+1)).accept(MediaType.APPLICATION_JSON);
		
		//Verificação
		mvc.perform(request)
			.andExpect(status().isNotFound());
	}	
	
	@Test
	@DisplayName("Deve deletar um livro do banco de dados")
	public void deleteBook() throws Exception{
		//Cenário (Given)
		Book book = Book.builder().build();
		BDDMockito.given( bookService.getById( Mockito.anyLong() )).willReturn(Optional.of(book));
		
		//Execução (When)
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.delete(BOOK_API.concat("/"+1)).accept(MediaType.APPLICATION_JSON);
		
		//Verificação
		mvc.perform(request)
			.andExpect(status().isNoContent());
	}
	
	@Test
	@DisplayName("Deve retornar Not_Found ao tentar deletar um livro inexistente do banco de dados")
	public void tryDeleteBook() throws Exception{
		//Cenário (Given)
		BDDMockito.given( bookService.getById( Mockito.anyLong() )).willReturn(Optional.empty());
		
		//Execução (When)
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.delete(BOOK_API.concat("/"+1)).accept(MediaType.APPLICATION_JSON);
		
		//Verificação
		mvc.perform(request)
			.andExpect(status().isNotFound());
	}
	
	@Test
	@DisplayName("Deve atualizar um livro do banco de dados")
	public void updateBook() throws Exception{
		//Cenário (Given)
		Long id = 1l;
		Book book = Book.builder().id(id).title("O garoto estudioso").author("Escritor").isbn("1690401").build();
		String json = new ObjectMapper().writeValueAsString(book);
		
		Book updatingBook = Book.builder().id(id).author("Some author").title("Title").isbn("1690401").build();
		BDDMockito.given(bookService.getById(id)).willReturn(Optional.of(updatingBook));
		
		BDDMockito.given(bookService.update(updatingBook) ).willReturn(book);
							
		//Execução (When)
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.put(BOOK_API.concat("/"+1))
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.content(json);;
		
		//Verificação
		mvc.perform(request)
			.andExpect(status().isOk())
			.andExpect(jsonPath("id").value(id))
			.andExpect(jsonPath("title").value(book.getTitle()))
			.andExpect(jsonPath("author").value(book.getAuthor()))
			.andExpect(jsonPath("isbn").value(updatingBook.getIsbn()));
		
	}
	
	@Test
	@DisplayName("Deve retornar Not_Found ao tentar atualizar um livro inexistente do banco de dados")
	public void tryUpdateBook() throws Exception{
		//Cenário (Given)
				Long id = 1l;
				Book book = Book.builder().id(id).title("O garoto estudioso").author("Escritor").isbn("1690401").build();
				String json = new ObjectMapper().writeValueAsString(book);
				BDDMockito.given( bookService.getById( Mockito.anyLong() )).willReturn(Optional.empty());
				
				//Execução (When)
				MockHttpServletRequestBuilder request = MockMvcRequestBuilders.put(BOOK_API.concat("/"+1))
						.accept(MediaType.APPLICATION_JSON)
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON)
						.content(json);
				
				//Verificação
				mvc.perform(request)
					.andExpect(status().isNotFound());
	}
	
	@Test
	@DisplayName("Deve filtrar livros do banco de dados")
	public void findBooksTest() throws Exception {
		//Cenario
		Long id = 1l;
		
		Book book = Book.builder().id(id).title("O garoto estudioso").author("Escritor").isbn("1690401").build();
		BDDMockito.given( bookService.find( Mockito.any(Book.class), Mockito.any(Pageable.class) ))
			.willReturn( new PageImpl<Book>( Arrays.asList(book), PageRequest.of(0, 100), 1) );
		
		String queryString = String.format("?title=%s&author=%s&page=0&size=100", book.getTitle(), book.getAuthor() );
		
		//Execução (When)
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get(BOOK_API.concat(queryString))
				.accept(MediaType.APPLICATION_JSON);
		
		//Verificação
		mvc.perform(request)
			.andExpect( status().isOk() )
			.andExpect( jsonPath( "content", Matchers.hasSize(1) ))
			.andExpect( jsonPath("totalElements").value(1) )
			.andExpect( jsonPath("pageable.pageSize").value(100) )
			.andExpect( jsonPath("pageable.pageNumber").value(0) );
		
	}
}
