package com.book.info;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.web.reactive.config.EnableWebFlux;

@SpringBootApplication
@EnableWebFlux
public class BookServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(BookServiceApplication.class, args);
    }

    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        objectMapper.configure(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS, false);
        objectMapper.setSerializationInclusion(Include.NON_NULL);
        objectMapper.setSerializationInclusion(Include.NON_DEFAULT);

//    objectMapper.registerModule(new JodaModule());

        return objectMapper;
    }
//  @Bean
//  @Primary
//  public ObjectMapper objectMapper() {
//    ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();
//    objectMapper.setSerializationInclusion(Include.NON_NULL);
//    objectMapper.setSerializationInclusion(Include.NON_DEFAULT);
//    return objectMapper;
//  }

//  @Bean
//  public BookDb bookDb(ObjectMapper objectMapper) {
//    return new BookDb(objectMapper);
//  }
}
