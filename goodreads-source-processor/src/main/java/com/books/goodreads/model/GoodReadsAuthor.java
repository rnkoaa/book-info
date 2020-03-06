package com.books.goodreads.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode.Include;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(SnakeCaseStrategy.class)
@Builder(toBuilder = true)
public class GoodReadsAuthor {
    private String averageRating;

    @Include
    private String authorId;
    private String bookId;
    private String textReviewsCount;
    private String name;
    private String roleName;
    private String ratingsCount;
}
