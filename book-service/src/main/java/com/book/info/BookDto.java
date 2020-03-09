package com.book.info;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
@JsonInclude(Include.NON_DEFAULT)
@JsonNaming(SnakeCaseStrategy.class)
public class BookDto {
    private int id;
    private int reviewCount;
    private int ratingsCount;
    private String countryCode;
    private String languageCode;
    private boolean ebook;
    private double averageRating;
    private String description;
    private String format;
    private String link;
    private String url;
    private String imageUrl;
    private String publisher;

    @JsonProperty("num_pages")
    private String pageCount;

    private String publicationDate;

    private int publicationYear;
    private String editionInformation;
    private String isbn13;
    private String title;

    @JsonIgnore
    private String titleWithoutSeries;
    private String isbn;

    private List<AuthorDto> authors;
}
