//package br.com.santos.william.moviebattle.movie;
//
//import br.com.santos.william.moviebattle.omdb.OmdbService;
//import br.com.santos.william.moviebattle.omdb.dto.OmdbMovie;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.springframework.boot.SpringApplication;
//import org.springframework.boot.context.event.ApplicationReadyEvent;
//import org.springframework.test.context.junit.jupiter.SpringExtension;
//
//import java.util.List;
//import java.util.stream.Stream;
//
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.BDDMockito.given;
//import static org.mockito.Mockito.verify;
//import static org.mockito.Mockito.verifyNoMoreInteractions;
//
//@ExtendWith(SpringExtension.class)
//public class MovieDtoWarmUpUnitTest {
//
//    @Mock
//    private MovieService movieService;
//
//    @Mock
//    private OmdbService omdbService;
//
//    @InjectMocks
//    private MovieWarmUp warmUp;
//
//    @Test
//    public void warmUpShouldInsertAllMoviesInDatabase() {
//        var movie = new OmdbMovie();
//        movie.setTitle("unit-test");
//        var movies = List.of(movie);
//
//        given(omdbService.listAll()).willReturn(movies.stream());
//
//        warmUp.warmUp(new ApplicationReadyEvent(new SpringApplication(), null, null));
//
//        verify(movieService).insert(any());
//    }
//
//    @Test
//    public void warmUpShouldNotInsertAnyObjectWhenOmdbReturnsEmpty() {
//        given(omdbService.listAll()).willReturn(Stream.empty());
//
//        warmUp.warmUp(new ApplicationReadyEvent(new SpringApplication(), null, null));
//
//        verifyNoMoreInteractions(movieService);
//    }
//
//}
