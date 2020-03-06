package com.books.goodreads.cassandra.repository;

import com.books.goodreads.model.GoodReadsReview;
import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.AsyncResultSet;
import com.datastax.oss.driver.api.core.cql.BoundStatement;
import com.datastax.oss.driver.api.core.cql.PreparedStatement;
import com.datastax.oss.driver.api.core.cql.Row;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

public class GoodReadsReviewRepository extends CassandraRepository {

    static DateTimeFormatter dateTimeFormatter =
        DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss Z yyyy");
    static String INSERT_QUERY =
        "INSERT INTO book_info.reviews "
            + "(book_id, review_id, user_id, created_at, rating, review_text, vote_count, comment_count) "
            + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
    private final PreparedStatement preparedStatement;
    private final static String SELECT_REVIEW_BY_BOOK_ID = "SELECT book_id, review_id, user_id, created_at, rating, "
        + "review_text, vote_count, "
        + "comment_count "
        + "FROM "
        + "book_info.reviews WHERE book_id = ?";

    private final PreparedStatement selectReviewByBookId;

    public GoodReadsReviewRepository(CqlSession cqlSession) {
        super(cqlSession);
        preparedStatement = cqlSession.prepare(INSERT_QUERY);
        selectReviewByBookId = cqlSession.prepare(SELECT_REVIEW_BY_BOOK_ID);
    }

    public CompletableFuture<List<GoodReadsReview>> findReviewsByBookId(String bookId) {
        BoundStatement boundStatement = selectReviewByBookId.bind(NumberUtils.toInt(bookId));
        return executeFindStatement(boundStatement, rowReviewFunction);
    }

    Function<Row, GoodReadsReview> rowReviewFunction = row -> {
        int bookId = row.getInt("book_id");
        String reviewId = row.getString("review_id");
        double rating = row.getDouble("rating");
        String userId = row.getString("user_id");
        long commentCount = row.getLong("comment_count");
        long voteCount = row.getLong("vote_count");
        LocalDate createdDate = row.getLocalDate("created_at");
        return GoodReadsReview.builder().bookId(String.valueOf(bookId))
            .userId(userId)
            .id(reviewId)
            .rating(rating)
            .commentCount(commentCount)
            .voteCount(voteCount)
            .reviewText(row.getString("review_text"))
            .dateAdded(createdDate != null ? createdDate.toString() : "")
            .build();
    };

    public CompletableFuture<Optional<GoodReadsReview>> save(GoodReadsReview review) {
        int bookId = NumberUtils.toInt(review.getBookId());
        LocalDate localDate;
        if (StringUtils.isNotEmpty(review.getDateAdded())) {
            OffsetDateTime offsetDateTime =
                OffsetDateTime.parse(review.getDateAdded(), dateTimeFormatter);
            localDate = offsetDateTime.toLocalDate();
        } else {
            localDate = LocalDate.now();
        }
        BoundStatement boundStatement =
            preparedStatement.bind(
                bookId,
                review.getId(),
                review.getUserId(),
                localDate,
                review.getRating(),
                review.getReviewText().strip().trim(),
                review.getVoteCount(),
                review.getCommentCount());
        CompletionStage<AsyncResultSet> completionStage = executeStatement(boundStatement);
        return completionStage
            .toCompletableFuture()
            .thenApply(
                asyncResultSet -> {
                    if (asyncResultSet.wasApplied()) {
                        return Optional.of(review);
                    }
                    return Optional.<GoodReadsReview>empty();
                })
            .exceptionally(
                e -> {
                    System.out.println(e.getMessage());
                    System.out.println("error saving review: " + review.getId());
                    return Optional.empty();
                });
    }
}
