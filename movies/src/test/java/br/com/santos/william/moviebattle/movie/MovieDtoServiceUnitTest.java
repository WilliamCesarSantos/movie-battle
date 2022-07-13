//package br.com.santos.william.moviebattle.movie;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
//import org.springframework.test.context.junit.jupiter.SpringExtension;
//
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertNotNull;
//import static org.mockito.BDDMockito.given;
//import static org.mockito.Mockito.verify;
//
//@ExtendWith(SpringExtension.class)
//public class MovieDtoServiceUnitTest {
//
//    @Mock
//    private MovieRepository repository;
//
//    @InjectMocks
//    private MovieService service;
//
//    private MovieDto movieDto = new MovieDto();
//
//    @BeforeEach
//    public void setup() {
//        movieDto.setId("1");
//        movieDto.setGenre("unit-test");
//        movieDto.setName("unit-test");
//        movieDto.setRating(10f);
//        movieDto.setVotes(100);
//    }
//
//    @Test
//    public void listShouldCallsRepository() {
//        var pageable = Pageable.unpaged();
//        var page = Page.<MovieDto>empty();
//
//        given(repository.findAll(pageable)).willReturn(page);
//
//        service.list(pageable);
//
//        verify(repository).findAll(pageable);
//    }
//
//    @Test
//    public void findByIdShouldCallsRepository() {
//        given(repository.findById("1")).willReturn(Optional.of(movieDto));
//
//        var result = service.findById("1");
//
//        assertNotNull(result);
//        assertEquals(movieDto.getId(), result.get().getId());
//    }
//
//    @Test
//    public void listShouldCallRepositoryFindAll() {
//        service.list();
//
//        verify(repository).findAll();
//    }
//
//    @Test
//    public void insertShouldCallsRepositorySave() {
//        given(repository.save(movieDto)).willReturn(movieDto);
//
//        var inserted = service.insert(movieDto);
//
//        assertEquals(movieDto.getId(), inserted.getId());
//        assertEquals(movieDto.getGenre(), inserted.getGenre());
//        assertEquals(movieDto.getName(), inserted.getName());
//        assertEquals(movieDto.getRating(), inserted.getRating());
//        assertEquals(movieDto.getVotes(), inserted.getVotes());
//    }
//}
