package com.api.LibraryAPI.repository;

import static org.assertj.core.api.Assertions.assertThat;
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
		String isbn = "1433";
		Book book = Book.builder().author("Ednaldo").title("Uat is the brother").isbn("1433").build();
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
		String isbn = "1433";
		
		//Execucao
		boolean exists = repository.existsByIsbn(isbn);
		
		//Verificacao
		assertThat(exists).isFalse();
	}
}
