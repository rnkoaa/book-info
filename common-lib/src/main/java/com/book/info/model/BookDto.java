package com.book.info.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import java.util.List;
import lombok.Builder;
import lombok.Value;

@Value
@Builder(toBuilder = true)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonDeserialize(builder = BookDto.BookDtoBuilder.class)
@JsonNaming(SnakeCaseStrategy.class)
public class BookDto {

    int id;
    String title;
    String edition;
    boolean ebook;
    double averageRating;
    String description;
    String format;
    String link;
    String url;
    String imageUrl;
    String publisher;
    int publicationYear;
    String isbn;
    String isbn13;
    int ratingCount;
    int reviewCount;
    int numPages;
    List<AuthorDto> authors;


    @JsonPOJOBuilder(withPrefix = "")
    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonNaming(SnakeCaseStrategy.class)
    public static class BookDtoBuilder{

    }

}
