package com.books.goodreads.cassandra.repository;

import com.books.goodreads.model.GoodReadsAuthor;
import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.AsyncResultSet;
import com.datastax.oss.driver.api.core.cql.BoundStatement;
import com.datastax.oss.driver.api.core.cql.PreparedStatement;
import com.datastax.oss.driver.api.core.cql.Row;
import com.datastax.oss.driver.api.querybuilder.QueryBuilder;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;
import org.apache.commons.lang3.math.NumberUtils;

public class GoodReadsBookAuthorRepository extends CassandraRepository {

  static String INSERT_BOOK_AUTHOR_STMT =
      "INSERT INTO book_info.book_author( author_id, author_name, average_rating, ratings_count, reviews_count ) VALUES "
          + "(?, ?, ?, ?, ?)";
  static String SELECT_BY_BOOK_ID = QueryBuilder.selectFrom("book_info", "book_by_authors")
      .columns("book_id", "author_id", "author_name", "role_name")
      .whereColumn("book_id").isEqualTo(QueryBuilder.bindMarker()).asCql();
  private final PreparedStatement insertPreparedStatement;
  private final PreparedStatement selectPreparedStatement;

  public GoodReadsBookAuthorRepository(CqlSession cqlSession) {
    super(cqlSession);
    insertPreparedStatement = cqlSession.prepare(INSERT_BOOK_AUTHOR_STMT);
    selectPreparedStatement = cqlSession.prepare(SELECT_BY_BOOK_ID);
  }

  public CompletableFuture<Optional<GoodReadsAuthor>> save(GoodReadsAuthor bookAuthor) {
    Objects.requireNonNull(bookAuthor, "Cannot save a null book author");
    int authorId = NumberUtils.toInt(bookAuthor.getAuthorId());
    double averageRating = NumberUtils.toDouble(bookAuthor.getAverageRating());
    long textReviewsRatingCount = NumberUtils.toLong(bookAuthor.getTextReviewsCount());
    long ratingCount = NumberUtils.toLong(bookAuthor.getRatingsCount());

    BoundStatement boundStatement =
        insertPreparedStatement.bind(
            authorId, bookAuthor.getName(), averageRating, ratingCount, textReviewsRatingCount);

    CompletionStage<AsyncResultSet> completionStage = executeStatement(boundStatement);
    return completionStage
        .toCompletableFuture()
        .thenApply(
            asyncResultSet -> {
              if (asyncResultSet.wasApplied()) {
                return Optional.of(bookAuthor);
              }
              return Optional.empty();
            });
  }

  public CompletableFuture<List<GoodReadsAuthor>> findByBookId(String bookId) {
    BoundStatement boundStatement = selectPreparedStatement.bind(NumberUtils.toInt(bookId));
    return executeFindStatement(boundStatement, rowMapper);
  }

  Function<Row, GoodReadsAuthor> rowMapper = row -> {
    int bookId = row.getInt("book_id");
    int authorId = row.getInt("author_id");
    String authorName = row.getString("author_name");
    String roleName = row.getString("role_name");
    return GoodReadsAuthor.builder()
        .authorId(String.valueOf(authorId))
        .name(authorName)
        .roleName(roleName)
        .bookId(String.valueOf(bookId))
        .build();
  };
}
