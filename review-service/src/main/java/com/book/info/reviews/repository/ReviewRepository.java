package com.book.info.reviews.repository;

import com.book.info.reviews.GoodReadsReview;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

@Repository
public class ReviewRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReviewRepository.class);

    private final Connection connection;
    final PreparedStatement preparedStatement;
    private final ObjectMapper objectMapper;

    public ReviewRepository(Connection connection, ObjectMapper objectMapper) {
        this.connection = connection;
        this.objectMapper = objectMapper;
        try {
            this.preparedStatement = connection.prepareStatement("select data from book_review where book_id = ?");
        } catch (SQLException e) {
            LOGGER.error("error while preparing statement {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public List<GoodReadsReview> findByBookId(int bookId) throws SQLException, JsonProcessingException {
        this.preparedStatement.setInt(1, bookId);
        ResultSet rs = this.preparedStatement.executeQuery();

        // loop through the result set
        List<GoodReadsReview> reviewDtos = new ArrayList<>();
        while (rs.next()) {
            String data = rs.getString("data");
            if (StringUtils.isNotEmpty(data)) {
                reviewDtos.add(objectMapper.readValue(data, GoodReadsReview.class));
            }
        }
        return reviewDtos;
    }

}
