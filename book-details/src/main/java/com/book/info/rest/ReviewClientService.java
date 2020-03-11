package com.book.info.rest;

import com.book.info.model.ReviewDto;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ReviewClientService {

    @GET("/reviews/id/{id}")
    CompletableFuture<ReviewDto> find(@Path("id") String id);

    @GET("/reviews/{bookId}")
    CompletableFuture<ListApiResponse<ReviewDto>> findByBookId(@Path("bookId") int bookId);
}
