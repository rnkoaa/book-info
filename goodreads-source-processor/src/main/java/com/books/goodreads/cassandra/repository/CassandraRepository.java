package com.books.goodreads.cassandra.repository;

import com.books.goodreads.async.CompletableFutureHelpers;
import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.AsyncResultSet;
import com.datastax.oss.driver.api.core.cql.BoundStatement;
import com.datastax.oss.driver.api.core.cql.Row;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class CassandraRepository {

    private final CqlSession cqlSession;

    public CassandraRepository(CqlSession cqlSession) {
        this.cqlSession = cqlSession;
    }

    public CompletionStage<AsyncResultSet> executeStatement(BoundStatement preparedStatement) {
        return cqlSession.executeAsync(preparedStatement);
    }

    public <T> CompletableFuture<List<T>> executeFindStatement(BoundStatement preparedStatement, Function<Row, T> mapper) {
        CompletionStage<AsyncResultSet> asyncResultSetStage = cqlSession.executeAsync(preparedStatement);
        return asyncResultSetStage.thenCompose(RowCollector::new)
            .thenApply(results -> results.stream()
                .map(mapper)
                .collect(Collectors.toList()))
            .toCompletableFuture();
    }

    public <T> CompletableFuture<Stream<T>> streamExecute(BoundStatement preparedStatement, Function<Row, T> mapper) {
        CompletionStage<AsyncResultSet> asyncResultSetStage = cqlSession.executeAsync(preparedStatement);
        return asyncResultSetStage.thenCompose(RowCollector::new)
            .thenApply(results -> results.stream()
                .map(mapper))
            .toCompletableFuture();
    }

    public <T> CompletableFuture<List<T>> executeWithRowFilter(BoundStatement preparedStatement, Function<Row, T> mapper,
        Predicate<Row> rowFilter) {
        CompletionStage<AsyncResultSet> asyncResultSetStage = cqlSession.executeAsync(preparedStatement);
        return asyncResultSetStage.thenCompose(RowCollector::new)
            .thenApply(results -> results.stream()
                .filter(rowFilter)
                .map(mapper)
                .collect(Collectors.toList()))
            .toCompletableFuture();
    }

    public <T> CompletableFuture<List<T>> executeWithFilter(BoundStatement preparedStatement, Function<Row, T> mapper,
        Predicate<T> filterPredicate) {
        CompletionStage<AsyncResultSet> asyncResultSetStage = cqlSession.executeAsync(preparedStatement);
        return asyncResultSetStage.thenCompose(RowCollector::new)
            .thenApply(results -> results.stream()
                .map(mapper)
                .filter(filterPredicate)
                .collect(Collectors.toList()))
            .toCompletableFuture();
    }

    <T> CompletableFuture<Optional<T>> executeFindOneStatement(BoundStatement boundStatement, Function<Row, T> rowMapper) {
        CompletionStage<AsyncResultSet> asyncResultSetStage = cqlSession.executeAsync(boundStatement);
        return asyncResultSetStage
            .thenApply(asyncResultSet -> {
                if (asyncResultSet.wasApplied()) {
                    Row one = asyncResultSet.one();
                    T res = rowMapper.apply(one);
                    return Optional.of(res);
                }
                return Optional.<T>empty();
            })
            .toCompletableFuture();
    }


    public CompletableFuture<List<Row>> executeFindStatement(BoundStatement preparedStatement) {
        CompletionStage<AsyncResultSet> asyncResultSetStage = cqlSession.executeAsync(preparedStatement);
        return asyncResultSetStage.thenCompose(RowCollector::new)
            .toCompletableFuture();
    }


    protected CompletableFuture<List<AsyncResultSet>> executeStatements(List<BoundStatement> boundStatements) {
        List<CompletableFuture<AsyncResultSet>> futures =
            boundStatements.stream()
                .map(cqlSession::executeAsync)
                .map(CompletionStage::toCompletableFuture)
                .collect(Collectors.toList());

        return CompletableFutureHelpers.allOfExceptionally(futures);
    }

    <T> CompletableFuture<Optional<T>> handleSaveFuture(int id, T originalObject, CompletionStage<AsyncResultSet> completionStage) {
        return completionStage
            .toCompletableFuture()
            .thenApply(
                asyncResultSet -> {
                    if (asyncResultSet.wasApplied()) {
                        return Optional.of(originalObject);
                    }
                    return Optional.<T>empty();
                })
            .exceptionally(
                e -> {
                    System.out.println(e.getMessage());
                    System.out.println("error saving review: " + id);
                    return Optional.empty();
                });
    }

    private static class RowCollector extends CompletableFuture<List<Row>> {

        final List<Row> rows = new ArrayList<>();

        RowCollector(AsyncResultSet first) {
            consumePage(first);
        }

        void consumePage(AsyncResultSet page) {
            for (Row row : page.currentPage()) {
                rows.add(row);
            }
            if (page.hasMorePages()) {
                page.fetchNextPage().thenAccept(this::consumePage);
            } else {
                complete(rows);
            }
        }
    }


}
