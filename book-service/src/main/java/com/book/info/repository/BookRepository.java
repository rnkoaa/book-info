package com.book.info.repository;

import com.book.info.GoodReadsBook;
import com.book.info.GoodReadsBook.Author;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.Builder;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

@Repository
public class BookRepository {

    final String selectBookWithAuthors = "select book.id, book.data, book_author.data as author from book "
        + "join book_author ON book.id = book_author.book_id where book.id = ?";

    private static final Logger LOGGER = LoggerFactory.getLogger(BookRepository.class);

    private final Connection connection;
    final PreparedStatement preparedStatement;
    final PreparedStatement findByIdPreparedStatement;
    private final ObjectMapper objectMapper;


    public BookRepository(Connection connection, ObjectMapper objectMapper) {
        this.connection = connection;
        this.objectMapper = objectMapper;
        try {
            this.preparedStatement = connection.prepareStatement("select data from book");
            this.findByIdPreparedStatement = connection.prepareStatement(selectBookWithAuthors);
        } catch (SQLException e) {
            LOGGER.error("error while preparing statement {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public Optional<GoodReadsBook> find(int bookId) throws SQLException, JsonProcessingException {
        this.findByIdPreparedStatement.setInt(1, bookId);
        ResultSet rs = this.findByIdPreparedStatement.executeQuery();
        List<BookRes> bookRes = new ArrayList<>();
        while (rs.next()) {
            int id = rs.getInt("id");
            String data = rs.getString("data");
            String author = rs.getString("author");
            BookRes res = BookRes.builder().id(id).data(data).author(author).build();
            bookRes.add(res);
        }

        Map<Integer, List<BookRes>> grouped = bookRes.stream().collect(Collectors.groupingBy(r -> r.id));
        return grouped.values().stream()
            .map(bookValues -> {
                BookRes bookData = bookValues.get(0);
                try {
                    GoodReadsBook goodReadsBook = objectMapper.readValue(bookData.data, GoodReadsBook.class);
                    List<Author> authors = bookValues.stream()
                        .map(bv -> bv.author)
                        .map(it -> {
                            try {
                                return objectMapper.readValue(it, Author.class);
                            } catch (JsonProcessingException e) {
                                e.printStackTrace();
                            }
                            return null;
                        })
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());
                    goodReadsBook.setAuthors(authors);
                    return goodReadsBook;
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
                return null;
            })
            .filter(Objects::nonNull)
            .findFirst();
    }

    public List<GoodReadsBook> findBooks() throws SQLException, JsonProcessingException {
        ResultSet rs = this.preparedStatement.executeQuery();

        // loop through the result set
        List<GoodReadsBook> goodReadsBooks = new ArrayList<>();
//        rs.n
        while (rs.next()) {
            String data = rs.getString("data");
            if (StringUtils.isNotEmpty(data)) {
                goodReadsBooks.add(objectMapper.readValue(data, GoodReadsBook.class));
            }
        }
        return goodReadsBooks;
    }

    public List<GoodReadsBook> findBooks(int lastId, int limit) throws SQLException, JsonProcessingException {
        ResultSet rs = this.preparedStatement.executeQuery();

        // loop through the result set
        List<GoodReadsBook> goodReadsBooks = new ArrayList<>();
//        rs.n
        while (rs.next()) {
            String data = rs.getString("data");
            if (StringUtils.isNotEmpty(data)) {
                goodReadsBooks.add(objectMapper.readValue(data, GoodReadsBook.class));
            }
        }
        return goodReadsBooks;
    }

    @Builder
    static class BookRes {

        int id;
        String author;
        String data;
    }

    /*
    select id, data from book
where id > 7789
order by id
limit 10;
     */
}
