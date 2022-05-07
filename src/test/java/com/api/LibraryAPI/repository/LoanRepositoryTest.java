package com.api.LibraryAPI.repository;

import static org.assertj.core.api.Assertions.assertThat;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import com.api.LibraryAPI.models.Book;
import com.api.LibraryAPI.models.Loan;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
public class LoanRepositoryTest {
	
	@Autowired
	private LoanRepository repository;
	
	@Autowired
	private TestEntityManager entityManager;
	
	@Test
	@DisplayName("Deve verificar se existe um empréstimo não devolvido para o livro")
	public void existsByBookAndNotReturnedTest() {
		//Cenario
		Loan loan = createAndPersistLoan(LocalDate.now());
		
		//Execucao
		boolean exists = repository.existsByBookAndNotReturned(loan.getBook());
		
		//Verificacao
		assertThat(exists).isTrue();
	}
	
	@Test
	@DisplayName("Deve buscar um empréstimo pelo isbn ou pelo cliente")
	public void findByIsbnOrCostumerTest() {
		//Cenario
		Loan loan = createAndPersistLoan(LocalDate.now());
		
		//Execucao
		Page<Loan> result = repository.findByBookIsbnOrCustomer("1604", "John Doe", PageRequest.of(0, 10));
		
		//Verificacao
		assertThat(result.getContent()).hasSize(1);
		assertThat(result.getContent()).contains(loan);
		assertThat(result.getPageable().getPageSize()).isEqualTo(10);
		assertThat(result.getPageable().getPageNumber()).isEqualTo(0);
		assertThat(result.getTotalElements()).isEqualTo(1);
	}
	
	@Test
	@DisplayName("Deve obter empréstimos cuja data emprestimo for menor ou igual a tres dias atras e nao retornados")
	public void findByLoanDateLessThanAndNotReturnedTest(){
		Loan loan = createAndPersistLoan( LocalDate.now().minusDays(5) );

	    List<Loan> result = repository.findByLoanDateLessThanAndNotReturned(LocalDate.now().minusDays(4));

	    assertThat(result).hasSize(1).contains(loan);
	}
	
	@Test
	@DisplayName("Deve retornar vazio quando não houver emprestimos atrasados.")
	public void notFindByLoanDateLessThanAndNotReturnedTest(){
		createAndPersistLoan( LocalDate.now());

	    List<Loan> result = repository.findByLoanDateLessThanAndNotReturned(LocalDate.now().minusDays(4));

	    assertThat(result).isEmpty();
	}
	
	public Loan createAndPersistLoan(LocalDate date) {
		Book book = Book.builder().author("Ednaldo").title("Uat is the brother").isbn("1604").build();
		entityManager.persist(book);
				
		Loan loan = Loan.builder().date(LocalDate.now()).customer("John Doe").book(book).date(date).build();
		entityManager.persist(loan);
		
		return loan;
	}
}
