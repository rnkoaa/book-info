package com.book.info.service;

public class BookNotFoundException extends RuntimeException {

  public BookNotFoundException(String message) {
    super(message);
  }
}
