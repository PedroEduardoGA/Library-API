package com.api.LibraryAPI.service;

import java.util.Optional;
import org.springframework.stereotype.Service;
import com.api.LibraryAPI.exceptions.BusinessException;
import com.api.LibraryAPI.models.Book;
import com.api.LibraryAPI.repository.BookRepository;

@Service
public class BookServiceImpl implements BookService{

	private BookRepository repository;
	
	public BookServiceImpl(BookRepository repository) {
		this.repository = repository;
	}

	@Override
	public Book save(Book book) {
		if( repository.existsByIsbn(book.getIsbn()))
			throw new BusinessException("Isbn ja cadastrado");
		
		return repository.save(book);
	}

	@Override
	public Optional<Book> getById(Long id) {
		return Optional.empty();
	}

	@Override
	public void delete(Book book) {
		
	}

	@Override
	public Book update(Book book) {
		return null;
		
	}

}
