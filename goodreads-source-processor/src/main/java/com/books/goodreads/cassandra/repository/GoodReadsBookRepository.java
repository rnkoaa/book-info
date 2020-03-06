package com.books.goodreads.cassandra.repository;

import com.books.goodreads.model.GoodReadsBook;
import com.books.goodreads.model.GoodReadsBook.Author;
import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.AsyncResultSet;
import com.datastax.oss.driver.api.core.cql.BoundStatement;
import com.datastax.oss.driver.api.core.cql.PreparedStatement;
import com.datastax.oss.driver.api.core.cql.Row;
import com.datastax.oss.driver.api.querybuilder.QueryBuilder;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

public class GoodReadsBookRepository extends CassandraRepository {

    static String INSERT_AUTHOR_WITH_BOOKS =
        "insert into book_info.author_with_books(author_id, book_id, author_name, role_name, publication_year) values (?, ?, ?, ?, ?)";
    static String INSERT_BOOK_BY_AUTHORS =
        "insert into book_info.book_by_authors(author_id, book_id, author_name, role_name, publication_year) values (?, ?, ?,  ?, ?)";

    static String INSERT_BOOK_BY_ISBN =
        "insert into book_info.book_by_isbn(isbn, book_id, publication_year, title, url, link, image_url, description) values "
            + "(?, ?, ?, ?, ?, ?, ?, ?)";
    static String INSERT_BOOK =
        "insert into book_info.book(id, isbn, isbn_13, asin, edition, kindle_asin, description, format, link, url, title, "
            + "title_without_series, image_url, publisher, page_count, publication_year, publication_date, ebook, average_rating, "
            + "rating_count, review_count) values "
            + "(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    static String INSERT_BOOK_BY_ISBN_13 =
        "insert into book_info.book_by_isbn_13(isbn, book_id, publication_year, title, url, link, image_url, description) values "
            + "(?, ?, ?, ?, ?, ?, ?, ?)";

    static String selectQuery = QueryBuilder.selectFrom("book_info", "book")
        .columns("id", "isbn", "isbn_13", "asin", "edition", "kindle_asin", "description", "format", "link", "url", "title",
            "title_without_series", "image_url", "publisher", "page_count", "publication_year", "publication_date", "ebook",
            "average_rating", "rating_count", "review_count")
        .whereColumn("id").isEqualTo(QueryBuilder.bindMarker()).asCql();

    private final PreparedStatement insertAuthorWithBooksStmt;
    private final PreparedStatement insertBookByAuthorsStmt;
    private final PreparedStatement insertBookByIsbnStmt;
    private final PreparedStatement insertBookByIsbn13Stmt;
    private final PreparedStatement insertBookStmt;
    private final PreparedStatement selectQueryStmt;

    public GoodReadsBookRepository(CqlSession cqlSession) {
        super(cqlSession);
        System.out.println(selectQuery);
        insertAuthorWithBooksStmt = cqlSession.prepare(INSERT_AUTHOR_WITH_BOOKS);
        insertBookByAuthorsStmt = cqlSession.prepare(INSERT_BOOK_BY_AUTHORS);
        insertBookByIsbnStmt = cqlSession.prepare(INSERT_BOOK_BY_ISBN);
        insertBookByIsbn13Stmt = cqlSession.prepare(INSERT_BOOK_BY_ISBN_13);
        insertBookStmt = cqlSession.prepare(INSERT_BOOK);
        selectQueryStmt = cqlSession.prepare(selectQuery);
    }

    public CompletableFuture<Optional<GoodReadsBook>> save(GoodReadsBook book) {
//    int bookId = NumberUtils.toInt(book.getId());
//    if (book.getAuthors() != null && book.getAuthors().size() > 0) {
//      saveBookAuthors(book, insertAuthorWithBooksStmt);
//      saveBookAuthors(book, insertBookByAuthorsStmt);
//    }

//    if (StringUtils.isNotEmpty(book.getIsbn())) {
//      CompletableFuture<Optional<Book>> optionalCompletableFuture = saveBookByIsbn(book, insertBookByIsbnStmt);
//      Optional<Book> join = optionalCompletableFuture.join();
//      if (join.isPresent()) {
//        // do nothing
//      }
//    }
        if (StringUtils.isNotEmpty(book.getIsbn13())) {
            CompletableFuture<Optional<GoodReadsBook>> optionalCompletableFuture = saveBookByIsbn13(book, insertBookByIsbn13Stmt);
            Optional<GoodReadsBook> join = optionalCompletableFuture.join();
            if (join.isPresent()) {
                // do nothing
            }
        }

        return saveBook(book, insertBookStmt);
    }

    private CompletableFuture<Optional<GoodReadsBook>> saveBook(GoodReadsBook book, PreparedStatement insertBookStmt) {
        int bookId = NumberUtils.toInt(book.getId());
        String isbn = (book.getIsbn() != null) ? book.getIsbn() : "";
        String isbn13 = (book.getIsbn13() != null) ? book.getIsbn13() : "";
        String asin = (book.getAsin() != null) ? book.getAsin() : "";
        String edition = (book.getEditionInformation() != null) ? book.getEditionInformation() : "";
        String kindleAsin = (book.getKindleAsin() != null) ? book.getKindleAsin() : "";
        String description = (book.getDescription() != null) ? book.getDescription() : "";
        String format = (book.getFormat() != null) ? book.getFormat() : "";
        String link = (book.getLink() != null) ? book.getLink() : "";
        String url = (book.getUrl() != null) ? book.getUrl() : "";
        String title = (book.getTitle() != null) ? book.getTitle() : "";
        String titleWithoutSeries = (book.getTitleWithoutSeries() != null) ? book.getTitleWithoutSeries() : "";
        String imageUrl = (book.getImageUrl() != null) ? book.getImageUrl() : "";
        String publisher = (book.getPublisher() != null) ? book.getPublisher() : "";
        int pageCount = NumberUtils.toInt(book.getPageCount());
        int publicationYear = NumberUtils.toInt(book.getPublicationYear());
        String publicationDate = book.getPublicationDate();
        boolean isEbook = BooleanUtils.toBoolean(book.getIsEbook());
        double averageRating = NumberUtils.toDouble(book.getAverageRating());
        long ratingCount = NumberUtils.toLong(book.getRatingsCount());
        long reviewCount = NumberUtils.toLong(book.getReviewCount());

        BoundStatement boundStatement = insertBookStmt.bind(bookId, isbn, isbn13, asin, edition, kindleAsin, description,
            format, link, url, title,
            titleWithoutSeries, imageUrl, publisher, pageCount, publicationYear, publicationDate, isEbook, averageRating,
            ratingCount, reviewCount);
        CompletionStage<AsyncResultSet> completionStage = executeStatement(boundStatement);
        return handleSaveFuture(bookId, book, completionStage);
    }

    private CompletableFuture<Optional<GoodReadsBook>> saveBookByIsbn(GoodReadsBook book, PreparedStatement insertBookByIsbnStmt) {
        int bookId = NumberUtils.toInt(book.getId());
        int publicationYear = NumberUtils.toInt(book.getPublicationYear());
        String url = (book.getUrl() != null) ? book.getUrl() : "";
        String title = (book.getTitle() != null) ? book.getTitle() : "";
        String link = (book.getLink() != null) ? book.getLink() : "";
        String imageUrl = (book.getImageUrl() != null) ? book.getImageUrl() : "";
        String description = (book.getDescription() != null) ? book.getDescription() : "";
        BoundStatement boundStatement = insertBookByIsbnStmt
            .bind(book.getIsbn(), bookId, publicationYear, title, url, link, imageUrl, description);
        CompletionStage<AsyncResultSet> completionStage = executeStatement(boundStatement);
        return handleSaveFuture(bookId, book, completionStage);
    }

    private CompletableFuture<Optional<GoodReadsBook>> saveBookByIsbn13(GoodReadsBook book, PreparedStatement insertBookByIsbn13Stmt) {
        int bookId = NumberUtils.toInt(book.getId());
        int publicationYear = NumberUtils.toInt(book.getPublicationYear());
        String url = (book.getUrl() != null) ? book.getUrl() : "";
        String title = (book.getTitle() != null) ? book.getTitle() : "";
        String link = (book.getLink() != null) ? book.getLink() : "";
        String imageUrl = (book.getImageUrl() != null) ? book.getImageUrl() : "";
        String description = (book.getDescription() != null) ? book.getDescription() : "";
        BoundStatement boundStatement = insertBookByIsbn13Stmt
            .bind(book.getIsbn13(), bookId, publicationYear, title, url, link, imageUrl, description);
        CompletionStage<AsyncResultSet> completionStage = executeStatement(boundStatement);
        return handleSaveFuture(bookId, book, completionStage);
    }

    void saveBookAuthors(GoodReadsBook book, PreparedStatement preparedStatement) {
        int bookId = NumberUtils.toInt(book.getId());
        List<Author> authors = book.getAuthors();
        int publicationYear = NumberUtils.toInt(book.getPublicationYear());
        List<BoundStatement> boundStatements =
            authors.stream()
                .map(
                    s -> {
                        int authorId = NumberUtils.toInt(s.getId());
                        String authorRole =
                            (s.getRole() != null) ? s.getRole() : "";
                        String authorName =
                            (s.getName() != null) ? s.getName() : "";
                        return preparedStatement.bind(authorId, bookId, authorName, authorRole, publicationYear);
                    })
                .collect(Collectors.toList());

        CompletableFuture<List<AsyncResultSet>> listCompletableFuture =
            executeStatements(boundStatements);
        listCompletableFuture
            .thenApply(it ->
            {
                it.forEach(
                    res -> {
                        if (!res.wasApplied()) {
                            System.out.println("it was not applied");
                        }
                    });
                return Optional.empty();
            })
            .exceptionally(
                e -> {
                    System.out.println(e.getMessage());
                    System.out.println("error saving review: " + book.getId());
                    return Optional.empty();
                });
    }

    public CompletableFuture<Optional<GoodReadsBook>> findById(String bookId) {
//    "insert into book_info.book(id, isbn, isbn_13, asin, edition, kindle_asin, description, format, link, url, title, "
//        + "title_without_series, image_url, publisher, page_count, publication_year, publication_date, ebook, average_rating, "
//        + "rating_count, review_count) values "
        BoundStatement boundStatement = selectQueryStmt.bind(NumberUtils.toInt(bookId));
        return executeFindOneStatement(boundStatement, rowToBookMapper);
    }

    Function<Row, GoodReadsBook> rowToBookMapper = row -> GoodReadsBook.builder()
        .id(String.valueOf(row.getInt("id")))
        .asin(row.getString("asin"))
        .averageRating(String.valueOf(row.getDouble("average_rating")))
        .description(row.getString("description"))
        .isEbook(String.valueOf(row.getBoolean("ebook")))
        .editionInformation(row.getString("edition"))
        .format(row.getString("format"))
        .imageUrl(row.getString("image_url"))
        .isbn(row.getString("isbn"))
        .isbn13(row.getString("isbn_13"))
        .kindleAsin(row.getString("kindle_asin"))
        .link(row.getString("link"))
        .pageCount(String.valueOf(row.getInt("page_count")))
        .publicationYear(String.valueOf(row.getInt("publication_year")))
        .publisher(row.getString("publisher"))
        .ratingsCount(String.valueOf(row.getLong("rating_count")))
        .reviewCount(String.valueOf(row.getLong("review_count")))
        .title(row.getString("title"))
        .url(row.getString("url"))
        .build();
}
