package com.api.LibraryAPI.service;

import java.util.Optional;

import com.api.LibraryAPI.models.Book;

public interface BookService {

	Book save(Book any);

	Optional<Book> getById(Long id);

	void delete(Book book);

	Book update(Book book);

}
