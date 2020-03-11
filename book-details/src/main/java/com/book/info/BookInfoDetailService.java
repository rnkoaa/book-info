package com.book.info;

import com.book.info.model.BookDto;
import com.book.info.model.ReviewDto;
import com.book.info.rest.BookClientService;
import com.book.info.rest.ListApiResponse;
import com.book.info.rest.ReviewClientService;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import org.springframework.stereotype.Service;

@Service
public class BookInfoDetailService {

    private final ReviewClientService reviewClientService;
    private final BookClientService bookClientService;

    public BookInfoDetailService(ReviewClientService reviewClientService, BookClientService bookClientService) {
        this.reviewClientService = reviewClientService;
        this.bookClientService = bookClientService;
    }

    public Optional<BookDetailDto> find(int id, boolean withReviews) {
        CompletableFuture<BookDto> bookDtoCompletableFuture = bookClientService.find(id);
        if (withReviews) {
            CompletableFuture<ListApiResponse<ReviewDto>> reviewsFuture = reviewClientService.findByBookId(id);
            return bookDtoCompletableFuture
                .thenCombine(reviewsFuture, (book, reviews) -> {
                    if (reviews.getTotal() > 0) {
                        return Optional.of(new BookDetailDto(book, reviews.getData()));
                    }
                    return Optional.of(new BookDetailDto(book, List.of()));
                })
                .join();
        }
        CompletableFuture<Optional<BookDetailDto>> optionalCompletableFuture = bookDtoCompletableFuture.thenApply(bookDto -> {
            if (bookDto != null) {
                return Optional.of(new BookDetailDto(bookDto, List.of()));
            }
            return Optional.empty();
        });
        return optionalCompletableFuture.join();
    }
}
