package br.com.santos.william.moviebattle.omdb.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Page {

    private Integer totalResults;
    @JsonProperty("Search")
    private List<OmdbIdentifierMovie> identifiers;

    public Integer getTotalResults() {
        return totalResults;
    }

    public void setTotalResults(Integer totalResults) {
        this.totalResults = totalResults;
    }

    public List<OmdbIdentifierMovie> getIdentifiers() {
        return identifiers;
    }

    public void setIdentifiers(List<OmdbIdentifierMovie> identifiers) {
        this.identifiers = identifiers;
    }
}
