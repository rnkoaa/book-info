package com.books.goodreads.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.util.List;
import lombok.Builder;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(SnakeCaseStrategy.class)
@Builder(toBuilder = true)
public class GoodReadsBook {
    @JsonProperty("book_id")
    private String id;

    @JsonProperty("text_reviews_count")
    private String reviewCount;

    private String countryCode;
    private String languageCode;
    private String asin;
    private String isEbook;
    private String averageRating;
    private String kindleAsin;
    private String description;
    private String format;
    private String link;
    private String url;
    private String imageUrl;
    private String publisher;

    @JsonProperty("num_pages")
    private String pageCount;

    private String ratingsCount;
    private String publicationDay;
    private String publicationMonth;
    private String publicationYear;
    private String editionInformation;
    private String isbn13;
    private String title;
    private String titleWithoutSeries;
    private String isbn;

    @JsonIgnore
    private List<Author> authors;

    @JsonIgnore
    private List<GoodReadsReview> reviews;

    @JsonIgnore
    public String getPublicationDate() {
        return createDate(publicationYear, publicationMonth, publicationDay);
    }

    private static String createDate(String year, String month, String day) {
        if (StringUtils.isEmpty(year) && StringUtils.isEmpty(month) && StringUtils.isEmpty(day)) {
            return "";
        }
        if (StringUtils.isNotEmpty(year)) {
            if (StringUtils.isEmpty(month) && StringUtils.isEmpty(day)) {
                return year;
            }
            if (StringUtils.isNotEmpty(month)) {
                if (StringUtils.isEmpty(day)) {
                    return String.format("%s-%2s", year, month).replace(' ', '0');
                }
                return String.format("%s-%2s-%2s", year, month, day).replace(' ', '0');
            } else {
                return year;
            }
        }
        return "";
    }


    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    @Builder(toBuilder = true)
    @JsonNaming(SnakeCaseStrategy.class)
    public static class Author {

        @JsonProperty("author_id")
        private String id;

        private String role;

        private String name;
    }
}
