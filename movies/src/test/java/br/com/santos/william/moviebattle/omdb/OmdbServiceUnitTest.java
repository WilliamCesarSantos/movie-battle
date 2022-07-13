//package br.com.santos.william.moviebattle.omdb;
//
//
//import br.com.santos.william.moviebattle.omdb.dto.OmdbMovie;
//import br.com.santos.william.moviebattle.omdb.dto.Page;
//import br.com.santos.william.moviebattle.util.TestUtil;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.springframework.test.context.junit.jupiter.SpringExtension;
//import org.springframework.test.util.ReflectionTestUtils;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertNotNull;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.BDDMockito.given;
//
//@ExtendWith(SpringExtension.class)
//public class OmdbServiceUnitTest {
//
//    @InjectMocks
//    private OmdbService service;
//
//    @Mock
//    private OmdbClient client;
//
//    @BeforeEach
//    public void setup() {
//        ReflectionTestUtils.setField(service, "filters", new String[]{"unit", "test"});
//    }
//
//    @Test
//    public void listAllMovies() {
//        var identifiers = TestUtil.buildIdentifiers(5);
//
//        var page = new Page();
//        page.setTotalResults(5);
//        page.setIdentifiers(identifiers);
//
//        given(client.listIdentifier(any(), any())).willReturn(page);
//        given(client.get(any())).willReturn(new OmdbMovie());
//
//        var stream = service.listAll();
//        assertNotNull(stream);
//        assertEquals(10, stream.count());
//    }
//}
