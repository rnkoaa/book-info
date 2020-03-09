package com.book.info;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.PropertyNamingStrategy.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@Builder(toBuilder = true)
@JsonInclude(Include.NON_DEFAULT)
@JsonNaming(SnakeCaseStrategy.class)
public class AuthorDto {

    private int id;
    private String role;
    private String name;
}
