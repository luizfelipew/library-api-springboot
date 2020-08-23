package com.cursowendt.libraryapi.api.model.repository;

import com.cursowendt.libraryapi.api.model.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

}
