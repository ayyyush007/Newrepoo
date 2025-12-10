package com.ty.movie.repository;

import com.ty.movie.model.Movie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MovieRepository extends JpaRepository<Movie, Long> {

    /**
     * Simple, safe text search by title.
     * Kept for backward compatibility / simple use-cases.
     */
    Page<Movie> findByTitleContainingIgnoreCase(String title, Pageable pageable);

    /**
     * Flexible paginated search across common text fields.
     *
     * - If 'keyword' is null or empty, this returns all movies (pageable applies).
     * - Searches title, genre and description (case-insensitive).
     *
     * NOTE: do NOT include entity fields here that do not exist (e.g. 'director' caused a startup error previously).
     * If you add fields to Movie later (like director), add them to this query too.
     */
    @Query("""
        SELECT m
        FROM Movie m
        WHERE (:keyword IS NULL OR :keyword = '')
          OR LOWER(m.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
          OR LOWER(m.genre) LIKE LOWER(CONCAT('%', :keyword, '%'))
          OR LOWER(m.description) LIKE LOWER(CONCAT('%', :keyword, '%'))
        """)
    Page<Movie> search(@Param("keyword") String keyword, Pageable pageable);
}
