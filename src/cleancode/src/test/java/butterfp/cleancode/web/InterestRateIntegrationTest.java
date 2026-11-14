package butterfp.cleancode.web;

import butterfp.cleancode.CleancodeApplication;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

import static com.google.common.truth.Truth.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

class InterestRateIntegrationTest {
    private static final String CLIENT_ID = "550e8400-e29b-41d4-a716-446655440000";

    private static HttpServer upstream;

    private static ConfigurableApplicationContext context;

    private static MockMvc mvc;

    @BeforeAll
    static void startApplication() throws IOException {
        upstream = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        upstream.createContext("/clients/" + CLIENT_ID + "/profile",
                exchange -> sendJson(exchange, """
                        {"hasSberPrime":true}
                        """));
        // TODO Проверить параметр month в запросе к заглушке, чтобы тест обнаруживал запрос покупок за неверный месяц.
        upstream.createContext("/clients/" + CLIENT_ID + "/transactions/purchases",
                exchange -> sendJson(exchange, """
                        {"totalPurchases":50000}
                        """));
        upstream.start();
        String upstreamUrl = "http://127.0.0.1:" + upstream.getAddress().getPort();

        context = new SpringApplicationBuilder(CleancodeApplication.class).run(
                "--server.port=0",
                "--server.address=127.0.0.1",
                "--clients.profile.base-url=" + upstreamUrl,
                "--clients.transactions.base-url=" + upstreamUrl);
        mvc = MockMvcBuilders.webAppContextSetup((WebApplicationContext) context).build();
    }

    @AfterAll
    static void stopApplication() {
        try {
            if (context != null) {
                context.close();
            }
        } finally {
            if (upstream != null) {
                upstream.stop(0);
            }
        }
    }

    @Test
    void returnsRateCalculatedFromExternalClientData() throws Exception {
        var request = post("/api/v1/rate").contentType(MediaType.APPLICATION_JSON).content("""
                {"date":"2025-01-01","id":"%s"}
                """.formatted(CLIENT_ID));

        var response = mvc.perform(request).andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.getContentAsString()).isEqualTo("16");
    }

    @Test
    void rejectsRequestWithInvalidClientId() throws Exception {
        var request = post("/api/v1/rate").contentType(MediaType.APPLICATION_JSON).content("""
                {"date":"2025-01-01","id":"invalid"}
                """);

        var response = mvc.perform(request).andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(400);
    }

    private static void sendJson(HttpExchange exchange, String json) throws IOException {
        byte[] body = json.getBytes(StandardCharsets.UTF_8);
        try (exchange) {
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, body.length);
            exchange.getResponseBody().write(body);
        }
    }
}
