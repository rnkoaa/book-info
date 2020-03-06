package com.book.info.reviews;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "review")
public class ReviewServiceProperties {

    @Value("${review.database.url}")
    private String databaseUrl;
}
