package com.book.info.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
@JsonNaming(SnakeCaseStrategy.class)
@JsonDeserialize(builder = ReviewDto.ReviewDtoBuilder.class)
public class ReviewDto {

    private String userId;
    private int bookId;

    @JsonProperty("review_id")
    private String id;

    private double rating;
    private String reviewText;

    @JsonProperty("num_votes")
    private long voteCount;

    @JsonProperty("num_comments")
    private long commentCount;

    private String createdAt;

    @JsonPOJOBuilder(withPrefix = "")
    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonNaming(SnakeCaseStrategy.class)
    public static class ReviewDtoBuilder {

    }
}
