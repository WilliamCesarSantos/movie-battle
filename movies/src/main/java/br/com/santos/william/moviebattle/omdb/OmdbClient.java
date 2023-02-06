package br.com.santos.william.moviebattle.omdb;

import br.com.santos.william.moviebattle.omdb.dto.OmdbMovie;
import br.com.santos.william.moviebattle.omdb.dto.Page;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "omdb-client", url = "${feign.omdb.base.url}")
public interface OmdbClient {

    @GetMapping(value = "/identifier")
    Page listIdentifier();

    @GetMapping(value = "/movie/{identifier}")
    OmdbMovie get(@PathVariable("identifier") String identifier);

}
