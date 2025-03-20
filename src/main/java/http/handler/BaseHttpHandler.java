package http.handler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import http.adapter.DurationAdapter;
import http.adapter.LocalDateTimeAdapter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;

public class BaseHttpHandler {
    public static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .setPrettyPrinting()
            .serializeNulls().create();

    protected void sendText(HttpExchange exchange, String text, int codeStatus) throws IOException {
        byte[] response = text.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        exchange.sendResponseHeaders(codeStatus, response.length);
        exchange.getResponseBody().write(response);
        exchange.close();
    }

    protected String readText(HttpExchange exchange) throws IOException {
        return new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
    }

    protected void sendNotFound(HttpExchange exchange) throws IOException {
        sendText(exchange, "Ресурс не найден", 404);
    }

    protected void sendHasInteractions(HttpExchange exchange) throws IOException {
        sendText(exchange, "Задача пересекается с существующей", 406);
    }

    protected void sendServerError(HttpExchange exchange, String message) throws IOException {
        System.err.println("Ошибка сервера: " + message);
        new Exception().printStackTrace();
        sendText(exchange, message, 500);
    }
}
