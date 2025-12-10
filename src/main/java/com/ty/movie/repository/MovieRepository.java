package com.ty.movie.repository;

import com.ty.movie.model.Movie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovieRepository extends JpaRepository<Movie, Long> {

    /**
     * Simple, safe text search by title. Use this to avoid referencing missing fields.
     * If you later confirm additional fields (e.g. 'director', 'description'), you can add derived methods
     * or a custom @Query that matches your entity fields.
     */
    Page<Movie> findByTitleContainingIgnoreCase(String title, Pageable pageable);
}
