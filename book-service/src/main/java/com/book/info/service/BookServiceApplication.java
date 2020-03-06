package com.book.info.service;

import com.book.info.data.processor.BookDb;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.config.EnableWebFlux;

@SpringBootApplication
@EnableWebFlux
public class BookServiceApplication {
  public static void main(String[] args) {
    SpringApplication.run(BookServiceApplication.class, args);
  }

  @Bean
  public BookDb bookDb(ObjectMapper objectMapper) {
    return new BookDb(objectMapper);
  }
}
