package com.book.info.reviews;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@Builder(toBuilder = true)
@JsonNaming(SnakeCaseStrategy.class)
public class GoodReadsReview {

    private String userId;
    private String bookId;

    @JsonProperty("review_id")
    private String id;

    private double rating;
    private String reviewText;

    @JsonProperty("n_votes")
    private long voteCount;

    @JsonProperty("n_comments")
    private long commentCount;

    private String dateAdded;
}