package com.api.LibraryAPI.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import com.api.LibraryAPI.models.Book;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
public class BookRepositoryTest {

	@Autowired
	TestEntityManager entityManager;
	
	@Autowired
	BookRepository repository;
	
	@Test
	@DisplayName("Deve retornar true caso o isbn ja exista na base de dados")
	public void trueWhenIsbnExists() {
		//Cenario
		String isbn = "1604";
		Book book = Book.builder().author("Ednaldo").title("Uat is the brother").isbn("1604").build();
		entityManager.persist(book);
		
		//Execucao
		boolean exists = repository.existsByIsbn(isbn);
		
		//Verificacao
		assertThat(exists).isTrue();
	}
	
	@Test
	@DisplayName("Deve retornar false caso o isbn não exista na base de dados")
	public void falseWhenIsbnDoesntExists() {
		//Cenario
		String isbn = "1604";
		
		//Execucao
		boolean exists = repository.existsByIsbn(isbn);
		
		//Verificacao
		assertThat(exists).isFalse();
	}
	
	@Test
	@DisplayName("Deve retornar false caso o isbn não exista na base de dados")
	public void findByIdTest() {
		//Cenario
		Book book = Book.builder().author("Ednaldo").title("Uat is the brother").isbn("1604").build();
		entityManager.persist(book);
		
		//Execucao
		Optional<Book> foundBook = repository.findById(book.getId());
		
		//Verificacao
		assertThat(foundBook.isPresent()).isTrue();
		
	}
	
	@Test
	@DisplayName("Deve salvar um livro")
	public void saveBookTest() {
		//Cenario
		Book book = Book.builder().author("Ednaldo").title("Uat is the brother").isbn("1604").build();
		
		//Execucao
		Book savedBook = repository.save(book);
		
		//Verificacao
		assertThat(savedBook.getId()).isNotNull();
				
	}
	
	@Test
	@DisplayName("Deve deletar um livro")
	public void deleteBookTest() {
		//Cenario
		Book book = Book.builder().author("Ednaldo").title("Uat is the brother").isbn("1604").build();
		entityManager.persist(book);
		
		//Execucao
		Book foundBook = entityManager.find(Book.class, book.getId() );
		repository.delete( foundBook );
		Book deleteBook = entityManager.find(Book.class, book.getId() );
		
		//Verificacao
		assertThat(deleteBook).isNull();
	}	
	
}
