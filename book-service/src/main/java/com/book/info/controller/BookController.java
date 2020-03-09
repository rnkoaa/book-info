package com.book.info.controller;

import com.book.info.BookDto;
import com.book.info.service.BookService;
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

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping({"", "/"})
    public Flux<BookDto> findAll(
        @RequestParam(value = "page", required = false, defaultValue = "0") int page,
        @RequestParam(value = "size", required = false, defaultValue = "10") int size) {
        if (page > 0 || size != 10) {
//      return Flux.fromStream(this.bookDb.getBooks(page, size).stream());
            List<BookDto> books = bookService.find();
            return Flux.fromStream(books.stream());
        }
        List<BookDto> books = bookService.find();
        return Flux.fromStream(books.stream());
    }

    @GetMapping("/id/{id}")
    public Mono<BookDto> findById(@PathVariable(value = "id") int id) {
        Optional<BookDto> bookId = this.bookService.find(id);
        BookDto book =
            bookId.orElseThrow(
                () -> new BookNotFoundException("Book With Id " + id + " not found"));
        return Mono.just(book);
    }

    @GetMapping("/{isbn}")
    public Mono<BookDto> findAll(@PathVariable(value = "isbn") String isbn) {
        Optional<BookDto> bookByIsbn = this.bookService.findBookByIsbn(isbn);
        BookDto book =
            bookByIsbn.orElseThrow(
                () -> new BookNotFoundException("Book With Isbn " + isbn + " not found"));
        return Mono.just(book);
    }
}
