package com.book.info.reviews.controller;

import com.book.info.reviews.ReviewDto;
import com.book.info.reviews.service.ReviewService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @GetMapping("/{bookId}")
    public Mono<List<ReviewDto>> findByBook(@PathVariable("bookId") int bookId) {
        List<ReviewDto> reviewsByBook = reviewService.findReviewsByBook(bookId);
        if (reviewsByBook.size() > 0) {
            return Mono.just(reviewsByBook);
        }
        return Mono.empty();
    }
}
