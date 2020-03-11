package com.book.info;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "book-info")
public class BookInfoDetailProperties {

    @Value("${book.info.url}")
    private String bookInfoUrl;
    @Value("${review.info.url}")
    private String reviewUrl;
}
