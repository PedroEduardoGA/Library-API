package com.api.LibraryAPI.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.api.LibraryAPI.models.Book;
import com.api.LibraryAPI.models.Loan;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
public class LoanRepositoryTest {
	
	@Autowired
	TestEntityManager entityManager;
	
	@Autowired
	LoanRepository repository;
	
	@Test
	@DisplayName("Deve verificar se existe um empréstimo não devolvido para o livro")
	public void existsByBookAndNotReturnedTest() {
		//Cenario
		Book book = Book.builder().author("Ednaldo").title("Uat is the brother").isbn("1604").build();
		entityManager.persist(book);
		
		Loan loan = Loan.builder().costumer("John Doe").book(book).date(LocalDate.now()).build();
		entityManager.persist(loan);
		
		//Execucao
		boolean exists = repository.existsByBookAndNotReturned(book);
		
		//Verificacao
		assertThat(exists).isTrue();
		}
}
