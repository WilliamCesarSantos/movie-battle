package br.com.santos.william.moviebattle.movie;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

public class MovieServiceUnitTest {

    private final MovieRepository repository = Mockito.mock(MovieRepository.class);
    private MovieService service = new MovieService(repository);

    private Movie movie = new Movie();

    @BeforeEach
    public void setup() {
        movie.setId(1l);
        movie.setGenre("unit-test");
        movie.setName("unit-test");
        movie.setRank(10f);
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
        assertEquals(movie.getRank(), inserted.getRank());
        assertEquals(movie.getVotes(), inserted.getVotes());
        assertEquals(movie.calculateScore(), inserted.calculateScore());
    }
}
