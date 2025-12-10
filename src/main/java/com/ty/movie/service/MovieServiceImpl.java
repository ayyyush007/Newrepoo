package com.ty.movie.service;

import com.ty.movie.model.Movie;
import com.ty.movie.repository.MovieRepository;
import com.ty.movie.service.MovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;

@Service
public class MovieServiceImpl implements MovieService {

    private final MovieRepository movieRepository;

    @Autowired
    public MovieServiceImpl(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    @Override
    public Page<Movie> getPaginatedMovies(int pageNo, int pageSize, String sortField, String sortDir, String keyword) {

        if (!StringUtils.hasText(sortField)) {
            sortField = "title";
        }
        if (!StringUtils.hasText(sortDir)) {
            sortDir = "asc";
        }

        Sort sort = Sort.by(sortField);
        sort = sortDir.equalsIgnoreCase("desc") ? sort.descending() : sort.ascending();

        int safePage = Math.max(pageNo, 1);
        int safeSize = Math.max(pageSize, 1);

        Pageable pageable = PageRequest.of(safePage - 1, safeSize, sort);

        if (StringUtils.hasText(keyword)) {
            return movieRepository.findByTitleContainingIgnoreCase(keyword.trim(), pageable);
        } else {
            return movieRepository.findAll(pageable);
        }
    }

    @Override
    public Movie save(Movie movie) {
        return movieRepository.save(movie);
    }

    @Override
    public Movie saveMovie(Movie movie) {
        return save(movie);
    }

    @Override
    public Optional<Movie> findById(Long id) {
        return movieRepository.findById(id);
    }

    @Override
    public Optional<Movie> getMovieById(Long id) {
        return findById(id);
    }

    @Override
    public void deleteById(Long id) {
        movieRepository.deleteById(id);
    }

    @Override
    public void deleteMovieById(Long id) {
        deleteById(id);
    }

    @Override
    public List<Movie> getAllMovies() {
        return movieRepository.findAll(Sort.by(Sort.Direction.ASC, "title"));
    }

    @Override
    public List<Movie> search(String q) {
        if (!StringUtils.hasText(q)) {
            return getAllMovies();
        }
        Pageable large = PageRequest.of(0, 1000, Sort.by("title"));
        return movieRepository.findByTitleContainingIgnoreCase(q.trim(), large).getContent();
    }
}
