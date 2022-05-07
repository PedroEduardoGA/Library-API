package com.api.LibraryAPI.controller;

import java.util.List;
import java.util.stream.Collectors;
import javax.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import com.api.LibraryAPI.models.Book;
import com.api.LibraryAPI.models.BookDto;
import com.api.LibraryAPI.models.Loan;
import com.api.LibraryAPI.models.LoanDto;
import com.api.LibraryAPI.service.BookService;
import com.api.LibraryAPI.service.LoanService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
@Api("Book API")
@Slf4j
public class BookController {
	
	private final BookService bookService;
	private final LoanService loanService;
	private final ModelMapper modelMapper;

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	@ApiOperation("Create a book")
	public BookDto createBook(@RequestBody @Valid BookDto bookDto) {
		log.info("[POST] creating a book for isbn: {} ", bookDto.getIsbn());
		Book entity = modelMapper.map(bookDto, Book.class);
		entity = bookService.save(entity);
		
		return modelMapper.map(entity, BookDto.class);
	}
	
	@GetMapping
	@ApiOperation("Obtains all books")
	public Page<BookDto> find(BookDto dto, Pageable pageRequest){
		log.info("[GET] Getting all books");
		Book filter = modelMapper.map(dto, Book.class);
		Page<Book> result = bookService.find(filter, pageRequest);
		
		List<BookDto> list = result.getContent()
			.stream()
			.map( entity -> modelMapper.map(entity, BookDto.class))
			.collect(Collectors.toList());
		
		return new PageImpl<BookDto>(list , pageRequest, result.getTotalElements());
	}
	
	@PutMapping("{id}")
	@ApiOperation("Update a book by id")
	public BookDto Update(@PathVariable Long id, @RequestBody @Valid BookDto dto) {
		log.info("[UPDATE] updating the book: {} ", id);
		return bookService.getById(id).map( book -> {

            book.setAuthor(dto.getAuthor());
            book.setTitle(dto.getTitle());
            book = bookService.update(book);
            return modelMapper.map(book, BookDto.class);

        }).orElseThrow( () -> new ResponseStatusException(HttpStatus.NOT_FOUND) );
		
	}
	
	@DeleteMapping("{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@ApiOperation("Delete a book by id")
	public void delete(@PathVariable Long id) {
		log.info("[DELETE] delete a book by id: {} ", id);
		Book book = bookService.getById(id).orElseThrow( () -> new ResponseStatusException(HttpStatus.NOT_FOUND) );
		bookService.delete(book);
	}
	
	@GetMapping("{id}")
	@ApiOperation("Obtains book details by id")
	public BookDto get(@PathVariable Long id) {
		log.info("[GET] getting book details from book: {} ", id);
		return bookService.getById(id).map( book -> modelMapper.map(book, BookDto.class) )
				.orElseThrow( () -> new ResponseStatusException(HttpStatus.NOT_FOUND) );
	}
	
	@GetMapping("{id}/loans")
	@ApiOperation("Obtains loans by book")
	public Page<LoanDto> loansByBook(@PathVariable Long id, Pageable pageable){
		log.info("[GET] getting loans from book: {} ", id);
		Book book = bookService.getById(id).orElseThrow( () -> new ResponseStatusException(HttpStatus.NOT_FOUND) );
		Page<Loan> result = loanService.getLoansByBook(book, pageable);
		List<LoanDto> loans = result
				.getContent()
				.stream()
				.map( entity -> {
					Book loanBook = entity.getBook();
					BookDto bookDto = modelMapper.map(loanBook, BookDto.class);
					LoanDto loanDto = modelMapper.map(entity, LoanDto.class);
					loanDto.setBook(bookDto);
					return loanDto;
				}).collect(Collectors.toList());
		
		return new PageImpl<LoanDto>(loans,pageable,result.getTotalElements());
	}
	
}
