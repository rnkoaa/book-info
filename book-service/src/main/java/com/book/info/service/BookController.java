package com.book.info.service;

import com.book.info.data.processor.Book;
import com.book.info.data.processor.BookDb;
import java.util.List;
import java.util.Optional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/book")
public class BookController {

  private final BookDb bookDb;

  public BookController(BookDb bookDb) {
    this.bookDb = bookDb;
  }

  @GetMapping({"", "/"})
  public Flux<Book> findAll(
      @RequestParam(value = "page", required = false, defaultValue = "0") int page,
      @RequestParam(value = "size", required = false, defaultValue = "10") int size) {
    if (page > 0 || size != 10) {
      return Flux.fromStream(this.bookDb.getBooks(page, size).stream());
    }
    List<Book> books = this.bookDb.getBooks();

    return Flux.fromStream(books.stream());
  }

  @GetMapping("/{isbn}")
  public Mono<Book> findAll(@PathVariable(value = "isbn") String isbn) {
    Optional<Book> bookByIsbn = this.bookDb.getBookByIsbn(isbn);
    Book book =
        bookByIsbn.orElseThrow(
            () -> new BookNotFoundException("Book With Isbn " + isbn + " not found"));
    return Mono.just(book);
  }
}
