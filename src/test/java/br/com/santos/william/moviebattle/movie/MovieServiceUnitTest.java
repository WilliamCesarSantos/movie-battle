package br.com.santos.william.moviebattle.movie;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(SpringExtension.class)
public class MovieServiceUnitTest {

    @Mock
    private MovieRepository repository;

    @InjectMocks
    private MovieService service;

    private Movie movie = new Movie();

    @BeforeEach
    public void setup() {
        movie.setId(1l);
        movie.setGenre("unit-test");
        movie.setName("unit-test");
        movie.setRating(10f);
        movie.setVotes(100);
    }

    @Test
    public void listShouldCallsRepository() {
        var pageable = Pageable.unpaged();
        var page = Page.<Movie>empty();

        given(repository.findAll(pageable)).willReturn(page);

        service.list(pageable);

        verify(repository).findAll(pageable);
    }

    @Test
    public void findByIdShouldCallsRepository() {
        given(repository.findById(1l)).willReturn(Optional.of(movie));

        var result = service.findById(1l);

        assertNotNull(result);
        assertEquals(movie.getId(), result.get().getId());
    }

    @Test
    public void listShouldCallRepositoryFindAll() {
        service.list();

        verify(repository).findAll();
    }

    @Test
    public void insertShouldCallsRepositorySave() {
        given(repository.save(movie)).willReturn(movie);

        var inserted = service.insert(movie);

        assertEquals(movie.getId(), inserted.getId());
        assertEquals(movie.getGenre(), inserted.getGenre());
        assertEquals(movie.getName(), inserted.getName());
        assertEquals(movie.getRating(), inserted.getRating());
        assertEquals(movie.getVotes(), inserted.getVotes());
    }
}
