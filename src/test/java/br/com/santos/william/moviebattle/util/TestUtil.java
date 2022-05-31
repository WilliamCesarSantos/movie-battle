package br.com.santos.william.moviebattle.util;

import br.com.santos.william.moviebattle.movie.Movie;
import br.com.santos.william.moviebattle.omdb.dto.OmdbIdentifierMovie;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class TestUtil {

    public static List<Movie> buildMovies(Integer amount) {
        return IntStream.range(0, amount)
                .mapToObj(it -> {
                    var movie = new Movie();
                    movie.setId(UUID.randomUUID().toString());
                    movie.setName("unit-test" + it);
                    movie.setRating((float) (it * 100 / (it + 1)));
                    movie.setVotes(it * it);
                    movie.setGenre("unit-test" + it);
                    return movie;
                })
                .collect(Collectors.toList());
    }

    public static List<OmdbIdentifierMovie> buildIdentifiers(Integer amount) {
        return IntStream.range(0, amount)
                .mapToObj(it -> {
                    var identifier = new OmdbIdentifierMovie();
                    identifier.setImdbID(String.valueOf(it));
                    return identifier;
                })
                .collect(Collectors.toList());
    }
}
