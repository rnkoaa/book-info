package com.books.goodreads.cassandra;

import lombok.Data;

@Data
public class CassandraProperties {
    private CassandraAuth auth;

    @Data
    public static class CassandraAuth {
        private boolean enabled;
        private String username;
        private String password;
    }
}