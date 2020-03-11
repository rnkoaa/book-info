package com.book.info.reviews.service;

import com.book.info.reviews.GoodReadsReview;
import com.book.info.model.ReviewDto;
import com.book.info.reviews.repository.ReviewRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ReviewService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReviewService.class);

    private final ReviewRepository reviewRepository;

    public ReviewService(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    public List<ReviewDto> findReviewsByBook(int bookId) {
        try {
            List<GoodReadsReview> reviews = reviewRepository.findByBookId(bookId);
            return reviews.stream()
                .map(this::transform)
                .collect(Collectors.toList());
        } catch (SQLException ex) {
            LOGGER.info("exception when reading data: {}", ex.getMessage());
        } catch (JsonProcessingException e) {
            LOGGER.info("exception when processing json data: {}", e.getMessage());
        }
        return List.of();
    }

    private ReviewDto transform(GoodReadsReview goodReadsReview) {
        return ReviewDto.builder()
            .id(goodReadsReview.getId())
            .userId(goodReadsReview.getUserId())
            .commentCount(goodReadsReview.getCommentCount())
            .rating(goodReadsReview.getRating())
            .reviewText(goodReadsReview.getReviewText())
            .createdAt(goodReadsReview.getDateAdded())
            .build();
    }

    public List<ReviewDto> findReviewsByBook(int bookId, int page, int limit) {
//        Pageable pageableRequest = PageRequest.of(page, limit);
        return findReviewsByBook(bookId);
    }
}
