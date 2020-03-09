package com.book.info.controller;

public class BookNotFoundException extends RuntimeException {

  public BookNotFoundException(String message) {
    super(message);
  }
}
