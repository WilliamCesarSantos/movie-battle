package br.com.santos.william.moviebattle.movie;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@Service
public class MovieService {

    private final MovieRepository repository;

    public MovieService(MovieRepository repository) {
        this.repository = repository;
    }

    public Page<Movie> list(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Optional<Movie> findById(Long id) {
        return repository.findById(id);
    }

    public List<Movie> list() {
        return repository.findAll();
    }

    public Movie insert(@Valid Movie movie) {
        return repository.save(movie);
    }

}
