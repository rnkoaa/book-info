package com.book.info.controller;

import com.book.info.BookDetailDto;
import com.book.info.BookInfoDetailService;
import com.book.info.model.BookDto;
import java.util.Optional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/book")
public class BookInfoDetailController {

    private final BookInfoDetailService bookInfoDetailService;

    public BookInfoDetailController(BookInfoDetailService bookInfoDetailService) {
        this.bookInfoDetailService = bookInfoDetailService;
    }

    @GetMapping("/id/{id}")
    public Mono<BookDetailDto> findBookById(@PathVariable("id") int id,
        @RequestParam(value = "reviews", required = false, defaultValue = "false") boolean withReviews) {
        Optional<BookDetailDto> optionalBookDto = bookInfoDetailService.find(id, withReviews);
        return optionalBookDto.map(Mono::justOrEmpty).orElse(Mono.empty());
    }
}
