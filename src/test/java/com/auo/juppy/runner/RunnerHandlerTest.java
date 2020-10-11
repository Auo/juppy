package com.auo.juppy.runner;

import com.auo.juppy.result.RunnerResult;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import javax.net.ssl.SSLSession;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class RunnerHandlerTest {
    private static final URI EXAMPLE_URI = URI.create("https://example.com");

    @Test
    public void test200Response() throws IOException, InterruptedException {
        HttpClient defaultHttpClient = Mockito.mock(HttpClient.class);

        ArrayBlockingQueue<RunnerResult> queue = new ArrayBlockingQueue<>(2);

        HttpResponse<Object> response = new MockResponse<>(200, EXAMPLE_URI);
        when(defaultHttpClient.send(any(), any())).thenReturn(response);


        RunnerHandler.ConnectivityRunner cr = new RunnerHandler.ConnectivityRunner(EXAMPLE_URI, 1000, queue, UUID.randomUUID(), defaultHttpClient, null);

        cr.run();

        assertEquals(1, queue.size());
        assertEquals(200, queue.take().statusCode);
    }

    @Test
    public void test500Response() throws InterruptedException, IOException {
        HttpClient defaultHttpClient = Mockito.mock(HttpClient.class);

        ArrayBlockingQueue<RunnerResult> queue = new ArrayBlockingQueue<>(2);

        HttpResponse<Object> response = new MockResponse<>(500, EXAMPLE_URI);
        when(defaultHttpClient.send(any(), any())).thenReturn(response);

        RunnerHandler.ConnectivityRunner cr = new RunnerHandler.ConnectivityRunner(EXAMPLE_URI, 1000, queue, UUID.randomUUID(), defaultHttpClient, null);

        cr.run();

        assertEquals(1, queue.size());
        assertEquals(500, queue.take().statusCode);
    }

    @Test
    public void testRequestInterrupted() throws InterruptedException, IOException {
        HttpClient defaultHttpClient = Mockito.mock(HttpClient.class);

        ArrayBlockingQueue<RunnerResult> queue = new ArrayBlockingQueue<>(2);
        when(defaultHttpClient.send(any(), any())).thenThrow(new InterruptedException());

        RunnerHandler.ConnectivityRunner cr = new RunnerHandler.ConnectivityRunner(EXAMPLE_URI, 1000, queue, UUID.randomUUID(), defaultHttpClient, null);

        cr.run();

        assertEquals(1, queue.size());
        assertEquals(-1, queue.take().statusCode);
    }


    private static class MockResponse<T> implements HttpResponse<T> {
        private final int statusCode;
        private final URI uri;

        private MockResponse(int statusCode, URI uri) {
            this.statusCode = statusCode;
            this.uri = uri;
        }

        @Override
        public int statusCode() {
            return statusCode;
        }

        @Override
        public HttpRequest request() {
            return null;
        }

        @Override
        public Optional<HttpResponse<T>> previousResponse() {
            return Optional.empty();
        }

        @Override
        public HttpHeaders headers() {
            return null;
        }

        @Override
        public T body() {
            return null;
        }

        @Override
        public Optional<SSLSession> sslSession() {
            return Optional.empty();
        }

        @Override
        public URI uri() {
            return uri;
        }

        @Override
        public HttpClient.Version version() {
            return null;
        }
    }

}