package com.books.goodreads.cassandra;

import com.books.goodreads.cassandra.repository.GoodReadsBookAuthorRepository;
import com.books.goodreads.cassandra.repository.GoodReadsBookRepository;
import com.books.goodreads.cassandra.repository.GoodReadsReviewRepository;
import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.CqlSessionBuilder;
import com.datastax.oss.driver.api.core.config.DriverConfigLoader;
import java.io.File;
import java.nio.file.Paths;
import org.apache.commons.lang3.StringUtils;

public class CassandraComponent {

    public static CqlSession cqlSession(
        String additionalConfigFile, CassandraProperties cassandraProperties) {
        CqlSessionBuilder builder = CqlSession.builder();
        if (!StringUtils.isEmpty(additionalConfigFile)) {
            File file = Paths.get(additionalConfigFile).toFile();
            DriverConfigLoader driverConfigLoader = DriverConfigLoader.fromFile(file);
            builder.withConfigLoader(driverConfigLoader);

            if (cassandraProperties != null && cassandraProperties.getAuth() != null) {
                CassandraProperties.CassandraAuth auth = cassandraProperties.getAuth();
                if (auth.isEnabled()) {
                    builder.withAuthCredentials(auth.getUsername(), auth.getPassword());
                }
            }
        }

        return builder.build();
    }

    public static GoodReadsBookAuthorRepository goodReadsBookAuthorRepository(CqlSession cqlSession) {
        return new GoodReadsBookAuthorRepository(cqlSession);
    }

    public static GoodReadsReviewRepository goodReadsReviewRepository(CqlSession cqlSession) {
        return new GoodReadsReviewRepository(cqlSession);
    }

    public static GoodReadsBookRepository goodReadsBookRepository(CqlSession cqlSession) {
        return new GoodReadsBookRepository(cqlSession);
    }
}
