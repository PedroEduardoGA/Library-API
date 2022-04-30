package com.api.LibraryAPI.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import com.api.LibraryAPI.exceptions.BusinessException;
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
		Book book = Book.builder().author("Ednaldo").title("Uat is the brother").isbn("1604").build();
		Mockito.when(repository.existsByIsbn(Mockito.anyString())).thenReturn(false);
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
	
	@Test
	@DisplayName("Deve lançar erro de negócio ao tentar salvar livro com isbn duplicado")
	public void shouldNotSaveBookByDuplicateIsbn() {
		//Cenario
		Book book = Book.builder().author("Ednaldo").title("Uat is the brother").isbn("1604").build();
		Mockito.when(repository.existsByIsbn(Mockito.anyString())).thenReturn(true);
		
		//Execucao
		Throwable exe = Assertions.catchThrowable( () -> bookService.save(book) );
		
		//Verificacao
		assertThat(exe).isInstanceOfAny(BusinessException.class).hasMessage("Isbn ja cadastrado");
		Mockito.verify( repository , Mockito.never()).save(book);
	}
	
	@Test
	@DisplayName("Deve obter um livro por id")
	public void getByIdTest() {
		//Cenario
		Long id = 1l;
		Book book = Book.builder().author("Ednaldo").title("Uat is the brother").isbn("1604").build();
		book.setId(id);
		Mockito.when( repository.findById(id)).thenReturn(Optional.of(book));
		
		//Execucao
		Optional<Book> foundBook = bookService.getById(id);
		
		//Verificacoes
		assertThat( foundBook.isPresent() ).isTrue();
		assertThat( foundBook.get().getId()).isEqualTo(id);
		assertThat( foundBook.get().getTitle()).isEqualTo(book.getTitle());
		assertThat( foundBook.get().getAuthor()).isEqualTo(book.getAuthor());
		assertThat( foundBook.get().getIsbn()).isEqualTo(book.getIsbn());
		
	}
	
	@Test
	@DisplayName("Deve retornar vazio ao nao encontrar o livro id no banco de dados")
	public void bookNotFoundById() {
		//Cenario
		Long id = 1l;
		Mockito.when( repository.findById(id)).thenReturn(Optional.empty());
		
		//Execucao
		Optional<Book> book = bookService.getById(id);
		
		//Verificacoes
		assertThat( book.isPresent() ).isFalse();
		
	}
	
	@Test
	@DisplayName("Deve deletar um livro")
	public void deleteBook() {
		//Cenario
		Book book = Book.builder().id(Long.valueOf(1)).build();
		
		//Execucao
		org.junit.jupiter.api.Assertions.assertDoesNotThrow( ()-> bookService.delete(book) );
		
		//Verificacoes
		Mockito.verify(repository, Mockito.times(1)).delete(book);
	}
	
	@Test
	@DisplayName("Deve ocorrer erro ao tentar deletar livro inexistente")
	public void tryDeleteBook() {
		//Cenario
		Book book = new Book();
		
		//Execucao e Verificacao
		org.junit.jupiter.api.Assertions.assertThrows( IllegalArgumentException.class, ()-> bookService.delete(book) );
		
		Mockito.verify(repository, Mockito.never()).delete(book);
	}
	
	@Test
	@DisplayName("Deve ocorrer erro ao tentar atualizar livro inexistente")
	public void tryUpdateBook() {
		//Cenario
		Book book = new Book();
		
		//Execucao e Verificacao
		org.junit.jupiter.api.Assertions.assertThrows( IllegalArgumentException.class, ()-> bookService.update(book) );
		
		Mockito.verify(repository, Mockito.never()).save(book);
	}
	
	@Test
	@DisplayName("Deve atualizar um livro com sucesso")
	public void updateBookTest() {
		//Cenario
		Long id = Long.valueOf(1);
			//Livro a atualizar
			Book book = Book.builder().id(id).build();
		
		Book updatedBook = Book.builder().author("Ednaldo").title("Uat is the brother").isbn("1604").build();
		updatedBook.setId(id);
		Mockito.when( repository.save(book)).thenReturn(updatedBook);
		
		//Execucao
		Book updt = bookService.update(book);
		
		//Verificacao
		assertThat(updt.getId()).isEqualTo(updatedBook.getId());
		assertThat( updt.getTitle()).isEqualTo(updatedBook.getTitle());
		assertThat( updt.getAuthor()).isEqualTo(updatedBook.getAuthor());
		assertThat( updt.getIsbn()).isEqualTo(updatedBook.getIsbn());
				
	}
	
	@SuppressWarnings("unchecked")
	@Test
	@DisplayName("Deve filtra um livro pelas propriedades")
	public void findBookTest() {
		//Cenario
		Book book = new Book();
		List<Book> list = Arrays.asList(book);
		PageRequest pageRequest =  PageRequest.of(0, 10);
		
		Page<Book> page = new PageImpl<Book>(list, pageRequest, 1);
		Mockito.when( repository.findAll( Mockito.any(Example.class),Mockito.any(PageRequest.class )))
			.thenReturn(page);
		
		//Execucao
		Page<Book> result = bookService.find(book, pageRequest);
		
		//Verificacao
		assertThat( result.getTotalElements()).isEqualTo(1);
		assertThat(result.getContent()).isEqualTo(list);
		assertThat(result.getPageable().getPageNumber()).isEqualTo(0);
		assertThat(result.getPageable().getPageSize()).isEqualTo(10);
		
	}
	
	@Test
	@DisplayName("Deve obter um livro pelo isbn")
	public void getByIsbnTest() {
		String isbn = "321";
		when( repository.findByIsbn(isbn)).thenReturn( Optional.of( Book.builder().id(1l).isbn(isbn).build() ));
		
		Optional<Book> book = bookService.getBookByIsbn(isbn);
		
		assertThat(book.isPresent());
		assertThat(book.get().getId()).isEqualTo(1l);
		assertThat(book.get().getIsbn()).isEqualTo(isbn);
		verify( repository, times(1)).findByIsbn(isbn);
	}
}
