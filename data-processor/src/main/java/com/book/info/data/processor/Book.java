package com.book.info.data.processor;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import java.util.List;
import lombok.Builder;
import lombok.Value;

@Value
@Builder(toBuilder = true)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonDeserialize(builder = Book.BookBuilder.class)
public class Book {
  private final String title;
  private final String isbn;
  private final int pageCount;
  private final PublishedDate publishedDate;
  private final String thumbnailUrl;
  private final String shortDescription;
  private final String longDescription;
  private final String status;
  private final List<String> authors;
  private final List<String> categories;

  @JsonPOJOBuilder(withPrefix = "")
  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class BookBuilder {

  }
}
