package com.api.LibraryAPI.service;

import org.springframework.stereotype.Service;
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
		return repository.save(book);
	}

}
