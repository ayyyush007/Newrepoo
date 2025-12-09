package com.ty.movie.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name="movies")
public class Movie {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String title;

    @Min(1800)
    @Max(2100)
    private Integer year;

    @NotBlank
    private String genre;

    @Column(length = 2000)
    private String description;

    private String posterFilename;

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public Integer getYear() { return year; }
    public void setYear(Integer year) { this.year = year; }
    public String getGenre() { return genre; }
    public void setGenre(String genre) { this.genre = genre; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getPosterFilename() { return posterFilename; }
    public void setPosterFilename(String posterFilename) { this.posterFilename = posterFilename; }
}
