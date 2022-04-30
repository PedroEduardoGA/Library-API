package com.api.LibraryAPI.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.api.LibraryAPI.models.Book;

public interface BookRepository extends JpaRepository<Book, Long>{
	boolean existsByIsbn(String isbn);

	Optional<Book> findByIsbn(String isbn);
}
