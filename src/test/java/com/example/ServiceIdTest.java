package com.example;

import io.micronaut.http.HttpAttributes;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MutableHttpRequest;
import io.micronaut.http.annotation.Filter;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.client.RxHttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.filter.ClientFilterChain;
import io.micronaut.http.filter.HttpClientFilter;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.reactivestreams.Publisher;

import javax.inject.Inject;

@MicronautTest
class ServiceIdTest {

    @Filter(Filter.MATCH_ALL_PATTERN)
    public static class ServiceIdCheckingFilter implements HttpClientFilter {
        @Override
        public Publisher<? extends HttpResponse<?>> doFilter(MutableHttpRequest<?> request, ClientFilterChain chain) {
            Assertions.assertTrue(request.getAttribute(HttpAttributes.SERVICE_ID.toString()).isPresent());
            return chain.proceed(request);
        }
    }

    @Client(id = "declarative")
    public interface DeclarativeClient {
        @Get("/")
        String get();
    }

    @Inject
    private DeclarativeClient declarativeClient;

    @Inject
    @Client(id = "low-level")
    private RxHttpClient rxHttpClient;

    @Test
    public void testDeclarativeClient() {
        declarativeClient.get();
    }

    @Test
    public void testRxHttpClient() {
        rxHttpClient.retrieve("/").firstElement().blockingGet();
    }
}
