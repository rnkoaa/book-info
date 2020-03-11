package com.book.info.rest;

import com.book.info.model.BookDto;
import java.util.concurrent.CompletableFuture;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface BookClientService {

    @GET("/book/id/{id}")
    CompletableFuture<BookDto> find(@Path("id") int id);
}
