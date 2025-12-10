package com.ty.movie.service;

import com.ty.movie.model.Movie;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

public interface MovieService {

    /**
     * Paginated movie list. pageNo is 1-based.
     */
    Page<Movie> getPaginatedMovies(int pageNo, int pageSize, String sortField, String sortDir, String keyword);

    // CRUD / helpers
    Movie save(Movie movie);

    Movie saveMovie(Movie movie); // optional wrapper for older code

    Optional<Movie> findById(Long id);

    Optional<Movie> getMovieById(Long id); // optional wrapper

    void deleteById(Long id);

    void deleteMovieById(Long id); // optional wrapper

    List<Movie> getAllMovies();

    /**
     * Non-paginated search returning a List — kept for compatibility with controllers that call movieService.search(q).
     */
    List<Movie> search(String q);
}
