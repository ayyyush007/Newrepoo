package com.ty.movie.controller;

import com.ty.movie.model.Movie;
import com.ty.movie.service.MovieService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

@Controller
@RequestMapping("/movies")
public class MovieController {

    private final MovieService movieService;

    // Folder to store posters under /static/posters/ inside the project
    private final Path postersDir = Paths.get("src/main/resources/static/posters");

    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    // LIST MOVIES
    @GetMapping({"", "/"})
    public String list(@RequestParam(required = false) String q, Model model) {
        model.addAttribute("movies", movieService.search(q));
        model.addAttribute("q", q);
        return "movies/list";
    }

    // VIEW MOVIE — handles Optional<Movie>
    @GetMapping("/view/{id}")
    public String view(@PathVariable Long id, Model model, RedirectAttributes ra) {
        Optional<Movie> opt = movieService.findById(id);
        if (opt.isEmpty()) {
            ra.addFlashAttribute("error", "Movie not found");
            return "redirect:/movies";
        }
        model.addAttribute("movie", opt.get());
        return "movies/view";
    }

    // SHOW ADD FORM
    @GetMapping("/new")
    public String createForm(Model model) {
        if (!model.containsAttribute("movie")) {
            model.addAttribute("movie", new Movie());
        }
        return "movies/form";
    }

    // SHOW EDIT FORM — handles Optional<Movie>
    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model, RedirectAttributes ra) {
        Optional<Movie> opt = movieService.findById(id);
        if (opt.isEmpty()) {
            ra.addFlashAttribute("error", "Movie not found");
            return "redirect:/movies";
        }
        model.addAttribute("movie", opt.get());
        return "movies/form";
    }

    // SAVE MOVIE (ADD/EDIT)
    @PostMapping("/save")
    public String save(
            @Valid @ModelAttribute("movie") Movie movie,
            BindingResult br,
            @RequestParam(value = "poster", required = false) MultipartFile poster,
            RedirectAttributes ra) {

        // VALIDATION FAILED
        if (br.hasErrors()) {
            ra.addFlashAttribute("org.springframework.validation.BindingResult.movie", br);
            ra.addFlashAttribute("movie", movie);

            if (movie.getId() != null) {
                return "redirect:/movies/edit/" + movie.getId();
            }
            return "redirect:/movies/new";
        }

        // POSTER UPLOAD
        if (poster != null && !poster.isEmpty()) {
            try {
                if (!Files.exists(postersDir)) {
                    Files.createDirectories(postersDir);
                }

                String original = StringUtils.cleanPath(poster.getOriginalFilename());
                String filename = System.currentTimeMillis() + "_" + original;

                Path target = postersDir.resolve(filename);
                Files.copy(poster.getInputStream(), target);

                movie.setPosterFilename("/posters/" + filename);

            } catch (IOException e) {
                e.printStackTrace();
                ra.addFlashAttribute("error", "Poster upload failed");
                return "redirect:/movies";
            }

        } else if (movie.getId() != null) {
            // Keep existing poster if editing
            Optional<Movie> old = movieService.findById(movie.getId());
            old.ifPresent(m -> movie.setPosterFilename(m.getPosterFilename()));
        }

        movieService.save(movie);
        ra.addFlashAttribute("success", "Movie saved successfully");

        return "redirect:/movies";
    }

    // DELETE MOVIE
    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        movieService.deleteById(id);
        ra.addFlashAttribute("success", "Movie deleted");
        return "redirect:/movies";
    }
}
