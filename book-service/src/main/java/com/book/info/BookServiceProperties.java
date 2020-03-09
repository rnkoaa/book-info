package com.book.info;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "book")
public class BookServiceProperties {

    @Value("${book.database.url}")
    private String databaseUrl;
}
