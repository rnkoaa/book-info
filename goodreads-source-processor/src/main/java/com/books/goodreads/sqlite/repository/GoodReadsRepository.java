package com.books.goodreads.sqlite.repository;

import com.books.goodreads.cassandra.CassandraComponent;
import com.books.goodreads.cassandra.repository.GoodReadsBookAuthorRepository;
import com.books.goodreads.cassandra.repository.GoodReadsBookRepository;
import com.books.goodreads.cassandra.repository.GoodReadsReviewRepository;
import com.books.goodreads.model.GoodReadsAuthor;
import com.books.goodreads.model.GoodReadsBook;
import com.books.goodreads.model.GoodReadsBook.Author;
import com.books.goodreads.model.GoodReadsReview;
import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.shaded.guava.common.io.Files;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;
import org.apache.commons.lang3.math.NumberUtils;

public class GoodReadsRepository {


    public static void main(String[] args) throws IOException, InterruptedException, SQLException {
        String filePath = "jdbc:sqlite:data/book-info.db";
        BlockingQueue<GoodReadsBook> queue = new LinkedBlockingQueue<>(10000);
        ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();
        objectMapper.setSerializationInclusion(Include.NON_NULL);
        objectMapper.setSerializationInclusion(Include.NON_DEFAULT);
        Connection connection = connect(filePath);
        if (connection != null) {
            createTables(connection);
        }
        // read books from cassandra db and publish unto the queue
        readBooksFromCassandra(queue);

        // save books into sqlite
        saveToSqlite(connection, objectMapper, queue);

        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }

    private static void saveToSqlite(Connection connection, ObjectMapper objectMapper,
        BlockingQueue<GoodReadsBook> queue) throws InterruptedException, SQLException, JsonProcessingException {

//    while (value = queue.take()) { doSomething(value); }
        int totalReceived = 0;
        while (true) {
            if (connection != null) {
                GoodReadsBook book = queue.take();
                totalReceived++;
                insertBook(connection, objectMapper, book);

                System.out.println("Total Received: " + totalReceived);
            }
        }


    }

    /**
     * Create a new table in the test database
     */
    public static void createTables(Connection connection) {

        // SQL statement for creating a new table
        String[] sql = {
            "CREATE TABLE IF NOT EXISTS book (id integer PRIMARY KEY, data text);",
            "CREATE TABLE IF NOT EXISTS book_author (book_id integer, author_id integer, data text, PRIMARY KEY(book_id, author_id));",
            "CREATE TABLE IF NOT EXISTS book_review (book_id integer, review_id text, data text, PRIMARY KEY(book_id, review_id));",
        };

        try {
            Statement statement = connection.createStatement();
            for (String query : sql) {
                statement.execute(query);
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }

//    try (Connection conn = DriverManager.getConnection(url);
//        Statement stmt = conn.createStatement()) {
//      // create a new table
//      stmt.execute(sql);
//    } catch (SQLException e) {
//      System.out.println(e.getMessage());
//    }
    }

    /**
     * select all rows in the warehouses table
     */
//  public void selectAll(){
//    String sql = "SELECT id, name, capacity FROM warehouses";
//
//    try (Connection conn = this.connect();
//        Statement stmt  = conn.createStatement();
//        ResultSet rs    = stmt.executeQuery(sql)){
//
//      // loop through the result set
//      while (rs.next()) {
//        System.out.println(rs.getInt("id") +  "\t" +
//            rs.getString("name") + "\t" +
//            rs.getDouble("capacity"));
//      }
//    } catch (SQLException e) {
//      System.out.println(e.getMessage());
//    }
//  }


    /**
     * Connect to a sample database
     */
    public static Connection connect(String url) {
        Connection conn = null;
        try {
            // create a connection to the database
            conn = DriverManager.getConnection(url);
            System.out.println("Connection to SQLite has been established.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }

    public static void insertBook(Connection connection, ObjectMapper objectMapper, GoodReadsBook book)
        throws SQLException, JsonProcessingException {
        Map<String, PreparedStatement> insertStatements = Map.of(
            "bookInsert", connection.prepareStatement("INSERT INTO book(id, data) values (?, ?)"),
            "bookAuthorInsert", connection.prepareStatement("INSERT INTO book_author(book_id, author_id, data) values (?, ?, ?)"),
            "bookReviewInsert", connection.prepareStatement("INSERT INTO book_review(book_id, review_id, data) values (?, ?, ?)")
        );

        List<Author> authors = book.getAuthors();
        List<GoodReadsReview> reviews = book.getReviews();

        // make a copy of the book and remove all authors/reviews since we do not want them in the json.
        GoodReadsBook bookCopy = book.toBuilder()
            .reviews(List.of())
            .build();
        int bookId = NumberUtils.toInt(bookCopy.getId());

        insertBookAuthors(objectMapper, authors, bookId, insertStatements);
        insertBookReviews(objectMapper, reviews, bookId, insertStatements);

        String bookJson = objectMapper.writeValueAsString(bookCopy);
        PreparedStatement bookInsert = insertStatements.get("bookInsert");
        bookInsert.setInt(1, bookId);
        bookInsert.setString(2, bookJson);
        bookInsert.execute();
    }

    private static void insertBookAuthors(ObjectMapper objectMapper, List<Author> authors, int bookId,
        Map<String, PreparedStatement> insertStatements)
        throws JsonProcessingException, SQLException {
        PreparedStatement bookAuthorInsert = insertStatements.get("bookAuthorInsert");
        if (bookAuthorInsert == null) {
            throw new IllegalArgumentException("Book Author Insert PreparedStatement not found");
        }
        for (Author author : authors) {
            int authorId = NumberUtils.toInt(author.getId());
            String authorJson = objectMapper.writeValueAsString(author);
            bookAuthorInsert.setInt(1, bookId);
            bookAuthorInsert.setInt(2, authorId);
            bookAuthorInsert.setString(3, authorJson);
            bookAuthorInsert.addBatch();
        }
        bookAuthorInsert.executeBatch();
    }

    private static void insertBookReviews(ObjectMapper objectMapper, List<GoodReadsReview> reviews, int bookId,
        Map<String, PreparedStatement> insertStatements)
        throws JsonProcessingException, SQLException {
        PreparedStatement bookReviewInsert = insertStatements.get("bookReviewInsert");
        if (bookReviewInsert == null) {
            throw new IllegalArgumentException("Book Author Insert PreparedStatement not found");
        }
        for (GoodReadsReview review : reviews) {
            String authorJson = objectMapper.writeValueAsString(review);
            bookReviewInsert.setInt(1, bookId);
            bookReviewInsert.setString(2, review.getId());
            bookReviewInsert.setString(3, authorJson);
            bookReviewInsert.addBatch();
        }
        bookReviewInsert.executeBatch();
    }

    static void readBooksFromCassandra(BlockingQueue<GoodReadsBook> queue) throws IOException {
        CqlSession cqlSession = CassandraComponent.cqlSession("", null);
        GoodReadsReviewRepository reviewRepository = CassandraComponent.goodReadsReviewRepository(cqlSession);
        GoodReadsBookRepository bookRepository = CassandraComponent.goodReadsBookRepository(cqlSession);
        GoodReadsBookAuthorRepository bookAuthorRepository = CassandraComponent.goodReadsBookAuthorRepository(cqlSession);
        List<String> bookIds = getBookIds();
        List<String> cleanedIds = bookIds.stream()
            .map(String::trim)
            .collect(Collectors.toList());

        cleanedIds.forEach(bookId -> {

            CompletableFuture<List<GoodReadsReview>> reviewFuture = reviewRepository.findReviewsByBookId(bookId);
            CompletableFuture<Optional<GoodReadsBook>> bookFuture = bookRepository.findById(bookId);
            CompletableFuture<List<GoodReadsAuthor>> bookAuthorFuture = bookAuthorRepository.findByBookId(bookId);
            CompletableFuture<Optional<GoodReadsBook>> bookResult = bookFuture
                .thenCombine(bookAuthorFuture, (optionalBook, bookAuthors) -> optionalBook
                    .map(book -> {
                        List<Author> authors = bookAuthors.stream()
                            .map(bAuthor -> Author.builder()
                                .id(bAuthor.getAuthorId())
                                .name(bAuthor.getName())
                                .role(bAuthor.getRoleName())
                                .build())
                            .collect(Collectors.toList());
                        return book.toBuilder()
                            .authors(authors)
                            .build();
                    }))
                .thenCombine(reviewFuture, (optionalBook, reviews) ->
                    optionalBook.map(b -> b.toBuilder().reviews(reviews).build()));

            bookResult.whenComplete((optionalResult, throwable) -> {
                if (throwable != null) {
                    System.out.printf("Got an exception: %s\n", throwable.getCause());
                }
                //
                if (optionalResult.isPresent()) {
                    GoodReadsBook book = optionalResult.get();
//          System.out.printf("BookId: %s, Isbn: %s, Isbn-13: %s\n", book.getId(), book.getIsbn(), book.getIsbn13());
//          System.out.printf("Number of Authors: %d\n", book.getAuthors().size());
//          System.out.printf("Number of Reviews: %d\n", book.getReviews().size());
                    try {
                        queue.put(book);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
//      Optional<Book> bookOptional = bookResult.join();
//      if (bookOptional.isPresent()) {
//        Book book = bookOptional.get();
//        System.out.printf("BookId: %s, Isbn: %s, Isbn-13: %s\n", book.getId(), book.getIsbn(), book.getIsbn13());
//        System.out.printf("Number of Authors: %d\n", book.getAuthors().size());
//        System.out.printf("Number of Reviews: %d\n", book.getReviews().size());
//      }
        });

        System.out.println("Done.");
//    reviewRepository.findReviewsByBookId()
    }

    static List<String> getBookIds() throws IOException {
        Path path = Paths.get("data/book-ids.csv");
        return Files.readLines(path.toFile(), Charset.defaultCharset());
    }
}
