package com.books.goodreads;

import com.books.goodreads.cassandra.repository.GoodReadsBookAuthorRepository;
import com.books.goodreads.cassandra.repository.GoodReadsBookRepository;
import com.books.goodreads.cassandra.repository.GoodReadsReviewRepository;
import com.books.goodreads.model.GoodReadsAuthor;
import com.books.goodreads.model.GoodReadsBook;
import com.books.goodreads.model.GoodReadsBook.Author;
import com.books.goodreads.model.GoodReadsReview;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

// class to process good reads json files.
public class GoodReadsJsonFileProcessor {

    public static void run() {
        //    CqlSession cqlSession = CassandraComponent.cqlSession("", null);
//    //        BookAuthorRepository bookAuthorRepository = new BookAuthorRepository(cqlSession);
////    ReviewRepository reviewRepository = new ReviewRepository(cqlSession);
////    processReviewFile(reviewRepository);
//
//    Map<String, BookAuthor> bookAuthors = loadAuthors();
//    System.out.printf("Loaded %d bookAuthors\n", bookAuthors.size());
//    BookRepository bookRepository = new BookRepository(cqlSession);
//    processBookFile(bookRepository, bookAuthors);

        // 2010-4-2
        // 2017-8-
        // 1996--
//    System.out.println(createDate("2010", "4", "2"));
//    System.out.println(createDate("2017", "", "2"));
//    System.out.println(createDate("2017", "8", null));
//    System.out.println(createDate("2017", "8", ""));
//    System.out.println(createDate("1996", null, null));
//    System.out.println(createDate("1996", "", ""));
    }

    private static ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    private static Map<String, GoodReadsAuthor> loadAuthors() throws FileNotFoundException {
        Map<String, GoodReadsAuthor> cache = new HashMap<>();
        String filePath = "data/book_authors.json";
        InputStream inputStream = new FileInputStream(filePath);
        List<GoodReadsAuthor> bucket = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            while (reader.ready()) {
                String line = reader.readLine();
                GoodReadsAuthor bookAuthor = transform(line, GoodReadsAuthor.class);
                cache.put(bookAuthor.getAuthorId(), bookAuthor);
            }

        } catch (IOException ignored) {
            System.out.println("io exception caught.");
        }
        return cache;
    }

    static int processBookFile(GoodReadsBookRepository bookRepository, Map<String, GoodReadsAuthor> bookAuthors) throws FileNotFoundException {
        String filePath = "data/books.json";
        InputStream inputStream = new FileInputStream(filePath);
        List<GoodReadsBook> bucket = new ArrayList<>();
        int totalCount = 0;
        int bucketCount = 0;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            while (reader.ready()) {
                totalCount++;
                String line = reader.readLine();
                var book = transform(line, GoodReadsBook.class);
                var updatedBook = assignAuthorNames(book, bookAuthors);
                bucket.add(updatedBook);
                if (bucket.size() == 10000) {
                    bucketCount++;
                    saveBooks(bookRepository, bucket, bucketCount);
                    bucket = new ArrayList<>();
                    Thread.sleep(500);
                }
            }
            bucketCount++;
            System.out.printf("There are %d items left in bucket\n", bucket.size());
            saveBooks(bookRepository, bucket, bucketCount);
            System.out.println("Done saving books.");
        } catch (IOException ignored) {
            System.out.println("io exception caught.");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.printf("TotalCount: %d, BucketCount: %d\n", totalCount, bucketCount);
        return 0;
    }

    private static GoodReadsBook assignAuthorNames(GoodReadsBook book, Map<String, GoodReadsAuthor> bookAuthors) {
        List<Author> authors = book.getAuthors();
        if (authors == null || authors.size() == 0) {
            return book;
        }

        List<Author> updatedAuthors = authors.stream()
            .map(it -> {
                String id = it.getId();
                GoodReadsAuthor bookAuthor = bookAuthors.get(id);
                if (bookAuthor == null) {
                    return it;
                }
                it.setName(bookAuthor.getName());
                return it;
            })
            .collect(Collectors.toList());
        book.setAuthors(updatedAuthors);
        return book;
    }

    private static void saveBooks(GoodReadsBookRepository bookRepository, List<GoodReadsBook> books, int bucketCount) {
        List<CompletableFuture<Optional<GoodReadsBook>>> savedReviews =
            books.stream().map(bookRepository::save).collect(Collectors.toList());

        savedReviews.forEach(
            b -> {
                Optional<GoodReadsBook> join = b.join();
                if (join.isPresent()) {
                }
            });

        System.out.printf("Done saving bucket %d\n", bucketCount);
    }

    static int processReviewFile(GoodReadsReviewRepository reviewRepository) throws IOException {
        String filePath = "data/reviews.json";
        InputStream inputStream = new FileInputStream(filePath);
        List<GoodReadsReview> bucket = new ArrayList<>();
        int totalCount = 0;
        int bucketCount = 0;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            while (reader.ready()) {
                totalCount++;
                String line = reader.readLine();
                GoodReadsReview review = transform(line, GoodReadsReview.class);
                bucket.add(review);
                if (bucket.size() == 20000) {
                    bucketCount++;
                    saveReviews(reviewRepository, bucket);
                    bucket = new ArrayList<>();
                }
            }
            bucketCount++;
            System.out.printf("There are %d items left in bucket\n", bucket.size());
            saveReviews(reviewRepository, bucket);
            System.out.println("Done.");
        } catch (IOException ignored) {
            System.out.println("io exception caught.");
        }
        System.out.printf("TotalCount: %d, BucketCount: %d\n", totalCount, bucketCount);
        return 0;
    }

    private static void saveReviews(GoodReadsReviewRepository reviewRepository, List<GoodReadsReview> reviews) {
        List<CompletableFuture<Optional<GoodReadsReview>>> savedReviews =
            reviews.stream().map(reviewRepository::save).collect(Collectors.toList());

        savedReviews.forEach(
            b -> {
                Optional<GoodReadsReview> join = b.join();
                if (join.isPresent()) {
                }
            });

        System.out.println("Done saving bucket.");
    }

    static int countReviewFile(GoodReadsBookAuthorRepository bookAuthorRepository) throws IOException {
        String filePath = "data/book_authors.json";
        InputStream inputStream = new FileInputStream(filePath);
        List<GoodReadsAuthor> bucket = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            while (reader.ready()) {
                String line = reader.readLine();
                GoodReadsAuthor bookAuthor = transform(line, GoodReadsAuthor.class);
                bucket.add(bookAuthor);
                if (bucket.size() == 10000) {
                    saveBookAuthors(bookAuthorRepository, bucket);
                    bucket = new ArrayList<>();
                }
            }

            System.out.printf("There are %d items left in bucket\n", bucket.size());
            saveBookAuthors(bookAuthorRepository, bucket);
            System.out.println("Done.");
        } catch (IOException ignored) {
            System.out.println("io exception caught.");
        }
        return 0;
    }

    private static void saveBookAuthors(
        GoodReadsBookAuthorRepository bookAuthorRepository, List<GoodReadsAuthor> bookAuthors) {
        List<CompletableFuture<Optional<GoodReadsAuthor>>> savedBooks =
            bookAuthors.stream().map(bookAuthorRepository::save).collect(Collectors.toList());

        savedBooks.forEach(
            b -> {
                Optional<GoodReadsAuthor> join = b.join();
                if (join.isPresent()) {
                }
            });

        System.out.println("Done saving bucket.");
    }

    static <T> T transform(String content, Class<T> tClass) throws JsonProcessingException {
        return objectMapper.readValue(content, tClass);
    }
}
