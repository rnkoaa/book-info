package com.book.info;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BookServiceConfiguration implements DisposableBean {

    Connection connection = null;
    private static final Logger LOGGER = LoggerFactory.getLogger(BookServiceConfiguration.class);

    @Bean
    public Connection connection(BookServiceProperties bookServiceProperties) {
        try {
            // create a connection to the database
            connection = DriverManager.getConnection(bookServiceProperties.getDatabaseUrl());
            LOGGER.info("Successfully Connected to the database.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return connection;
    }

    @Override
    public void destroy() throws Exception {
        LOGGER.info("destroying the sqlite connection due to shutdown.");
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException ex) {
            LOGGER.error(ex.getMessage());
        }
    }
}
