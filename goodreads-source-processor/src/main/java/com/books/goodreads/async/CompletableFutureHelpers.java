package com.books.goodreads.async;


import static java.util.stream.Collectors.toList;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class CompletableFutureHelpers {
    public static <T> CompletableFuture<List<T>> allOf(Collection<CompletableFuture<T>> futures) {
        return futures.stream()
            .collect(Collectors.collectingAndThen(
                toList(),
                l -> CompletableFuture.allOf(l.toArray(new CompletableFuture[0]))
                    .thenApply(__ -> l.stream()
                        .map(CompletableFuture::join)
                        .collect(Collectors.toList()))));
    }

    public static <T> CompletableFuture<List<T>> allOfExceptionally(Collection<CompletableFuture<T>> futures) {

        CompletableFuture<List<T>> result = new CompletableFuture<>();
        futures.forEach(f -> f
            .handle((__, ex) -> ex == null || result.completeExceptionally(ex)));

        allOf(futures).handle((r, ex) -> ex != null
            ? result.completeExceptionally(ex)
            : result.complete(r));

        return result;
    }
}