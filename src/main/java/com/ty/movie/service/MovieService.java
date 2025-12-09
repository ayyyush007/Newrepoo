package com.ty.movie.service;

import com.ty.movie.model.Movie;
import com.ty.movie.repository.MovieRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MovieService {

    private final MovieRepository repo;

    public MovieService(MovieRepository repo) {
        this.repo = repo;
    }

    public List<Movie> findAll() { return repo.findAll(); }

    public Optional<Movie> findById(Long id) { return repo.findById(id); }

    public Movie save(Movie movie) { return repo.save(movie); }

    public void deleteById(Long id) { repo.deleteById(id); }

    public List<Movie> search(String keyword) {
        return repo.findByTitleContainingIgnoreCase(keyword == null ? "" : keyword);
    }
}
