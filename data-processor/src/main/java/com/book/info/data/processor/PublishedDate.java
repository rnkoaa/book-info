package com.book.info.data.processor;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.Value;

@Value
@Builder(toBuilder = true)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonDeserialize(builder = PublishedDate.PublishedDateBuilder.class)
public class PublishedDate {
  @JsonProperty("$date")
  private final String date;

  @JsonPOJOBuilder(withPrefix = "")
  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class PublishedDateBuilder {}
}
