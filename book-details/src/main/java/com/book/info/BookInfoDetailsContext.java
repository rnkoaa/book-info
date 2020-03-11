package com.book.info;

import com.book.info.rest.BookClientService;
import com.book.info.rest.ReviewClientService;
import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

@Configuration
public class BookInfoDetailsContext {

    // https://medium.com/@eranda/spring-rest-client-with-reliability-and-instrumentation-ac6e40b84d75
    // TODO collect metrics to see how all endpoints are doing.
    private static OkHttpClient.Builder httpClient = new OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS);

    private static Retrofit.Builder retrofitBuilder
        = new Retrofit.Builder()
        .addConverterFactory(JacksonConverterFactory.create());

    public static <S> S createService(Class<S> serviceClass, String baseUrl) {
        OkHttpClient authenticatedClient = httpClient
            .build();

        Retrofit authenticatedRetrofit = retrofitBuilder.baseUrl(baseUrl).client(authenticatedClient).build();
        return authenticatedRetrofit.create(serviceClass);
    }

    @Bean
    public ReviewClientService reviewClientService(BookInfoDetailProperties bookInfoDetailProperties) {
        return createService(ReviewClientService.class, bookInfoDetailProperties.getReviewUrl());
    }

    @Bean
    public BookClientService bookClientService(BookInfoDetailProperties bookInfoDetailProperties) {
        return createService(BookClientService.class, bookInfoDetailProperties.getBookInfoUrl());
    }

}
