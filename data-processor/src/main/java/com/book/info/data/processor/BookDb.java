package com.book.info.data.processor;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BookDb {
  private final List<Book> books;

  public BookDb(ObjectMapper objectMapper) {
    this.books = loadBooks("data/books-v1.json", objectMapper);
  }

  private List<Book> loadBooks(String booksFilePath, ObjectMapper objectMapper) {
    InputStream resourceAsStream = getClass().getClassLoader().getResourceAsStream(booksFilePath);
    Objects.requireNonNull(resourceAsStream, "books file path cannot be null");
    JavaType bookType =
        objectMapper.getTypeFactory().constructCollectionType(List.class, Book.class);
    try {
      return objectMapper.readValue(resourceAsStream, bookType);
    } catch (IOException e) {
      e.printStackTrace();
    }
    return List.of();
  }

  public List<Book> getBooks() {
    return this.books;
  }

  public List<Book> getBooks(int page, int size) {
    Stream<Book> stream = this.books.stream();
    return stream.skip(page).limit(size).collect(Collectors.toList());
  }

  public Optional<Book> getBookByIsbn(String isbn) {
    return this.books.stream()
        .filter(Objects::nonNull)
        .filter(b -> b.getIsbn() != null && !b.getIsbn().isEmpty())
        .filter(b -> isbn.equals(b.getIsbn()))
        .findAny();
  }
}
