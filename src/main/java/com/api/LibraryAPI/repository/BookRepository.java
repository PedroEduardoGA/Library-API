package com.api.LibraryAPI.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.api.LibraryAPI.models.Book;

public interface BookRepository extends JpaRepository<Book, Long>{

}
