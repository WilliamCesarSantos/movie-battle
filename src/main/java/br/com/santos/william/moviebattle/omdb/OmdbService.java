package br.com.santos.william.moviebattle.omdb;

import br.com.santos.william.moviebattle.movie.Movie;
import br.com.santos.william.moviebattle.omdb.dto.OmdbIdentifierMovie;
import br.com.santos.william.moviebattle.omdb.dto.OmdbMovie;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.stream.IntStream;
import java.util.stream.Stream;

@Service
public class OmdbService {

    private final OmdbClient client;
    private final String[] filters;
    private Logger log = LoggerFactory.getLogger(getClass());

    public OmdbService(
            OmdbClient client,
            @Value("${feign.omdb.filters}") String[] filters
    ) {
        this.client = client;
        this.filters = filters;
    }

    public Stream<Movie> listAll() {
        log.warn("Executing omdb warmup. Wait this....");
        return Stream.of(filters)
                .parallel()
                .flatMap(this::search);
    }

    private Stream<Movie> search(String filter) {
        var pageLimit = this.discoveryPageLimit(filter);
        return IntStream.rangeClosed(1, pageLimit)
                .parallel()
                .mapToObj(pageIndex -> {
                    log.debug("Searching page: {} with filter: {}", pageIndex, filter);
                    return client.listIdentifier(filter, pageIndex);
                })
                .flatMap(page -> page.getIdentifiers().stream())
                .map(this::get)
                .map(this::convert);
    }

    private OmdbMovie get(OmdbIdentifierMovie identifier) {
        log.info("Search movie: {} ", identifier);
        return client.get(identifier.getImdbID());
    }

    private Movie convert(OmdbMovie omdb) {
        var movie = new Movie();
        movie.setRating(omdb.getImdbRating());
        movie.setVotes(omdb.getImdbVotes());
        movie.setName(omdb.getTitle());
        movie.setGenre(omdb.getGenre());
        return movie;
    }

    private Integer discoveryPageLimit(String filter) {
        var limit = 1;
        var pageSize = 10;
        var firstPage = client.listIdentifier(filter, 1);
        var totalResults = firstPage.getTotalResults();
        if (totalResults != null && totalResults > pageSize) {
            limit = Math.abs(firstPage.getTotalResults() / 10);
        }
        log.info("Found page: {} for filter: {}", limit, filter);
        return limit;
    }

}
