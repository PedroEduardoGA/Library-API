package com.api.LibraryAPI.service;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import com.api.LibraryAPI.models.Book;
import com.api.LibraryAPI.repository.BookRepository;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class BookServiceTest {
	
	BookService bookService;
	
	@MockBean
	BookRepository repository;
	
	@BeforeEach
	public void SetUp() {
		this.bookService = new BookServiceImpl(repository);
	}
	
	@Test
	@DisplayName("Deve salvar um livro")
	public void saveBookTest() {
		//Cenario
		Book book = Book.builder().author("Ednaldo").title("Uat is the brother").isbn("1433").build();
		Mockito.when(repository.save(book)).thenReturn(Book.builder()
				.id(Long.valueOf(12))
				.author("Ednaldo")
				.title("Uat is the brother")
				.isbn("1433").build());
		
		//Execucao
		Book savedBook = bookService.save(book);
		
		//Verificacao
		assertThat(savedBook.getId()).isNotNull();
		assertThat(savedBook.getAuthor()).isEqualTo("Ednaldo");
		assertThat(savedBook.getTitle()).isEqualTo("Uat is the brother");
		assertThat(savedBook.getIsbn()).isEqualTo("1433");
	}
}
