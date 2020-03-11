package com.book.info.reviews.controller;

import com.book.info.rest.ListApiResponse;
import com.book.info.model.ReviewDto;
import com.book.info.reviews.service.ReviewService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
    public Mono<ListApiResponse<ReviewDto>> findByBook(@PathVariable("bookId") int bookId,
        @RequestParam(value = "size", defaultValue = "10") int limit,
        @RequestParam(value = "page", defaultValue = "0") int page) {
        List<ReviewDto> reviewsByBook = reviewService.findReviewsByBook(bookId, page, limit);
        if (reviewsByBook.size() > 0) {
            ListApiResponse<ReviewDto> reviewDtoListApiResponse = ListApiResponse.fromList(reviewsByBook);
            return Mono.just(reviewDtoListApiResponse);
        }
        return Mono.just(ListApiResponse.empty());
    }
}
