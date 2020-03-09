package com.book.info.rest;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.util.List;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(SnakeCaseStrategy.class)
public class ListApiResponse<T> extends ApiResponse<List<T>> {

    int total;
    int page;
    int perPage;
    int totalPages;

    public static <T> ListApiResponse<T> fromList(List<T> data) {
        Objects.requireNonNull(data, "data cannot be null for response.");
        return new ListApiResponseBuilder<T>()
            .total(data.size())
            .data(data)
            .page(0)
            .perPage(data.size())
            .build();
    }

    public static <T> ListApiResponseBuilder<T> builder() {
        return new ListApiResponseBuilder<>();
    }

    public static <T> ListApiResponse<T> empty() {
        return new ListApiResponseBuilder<T>()
            .message("empty response")
            .build();
    }

    public static class ListApiResponseBuilder<T> {

        int total;
        List<T> data;
        int page;
        int perPage;
        private String message;
        int totalPages;

        public ListApiResponseBuilder<T> message(String message) {
            this.message = message;
            return this;
        }

        public ListApiResponseBuilder<T> data(List<T> data) {
            this.data = data;
            return this;
        }

        public ListApiResponseBuilder<T> totalPages(int totalPages) {
            this.totalPages = totalPages;
            return this;
        }

        public ListApiResponseBuilder<T> perPage(int perPage) {
            this.perPage = perPage;
            return this;
        }

        public ListApiResponseBuilder<T> page(int page) {
            this.page = page;
            return this;
        }

        public ListApiResponseBuilder<T> total(int total) {
            this.total = total;
            return this;
        }

        public ListApiResponse<T> build() {
            ListApiResponse<T> tListApiResponse = new ListApiResponse<>(total, page, perPage, totalPages);
            tListApiResponse.setData(data);
            if (message != null) {
                tListApiResponse.setMessage(message);
            }
            return tListApiResponse;
        }
    }
}
