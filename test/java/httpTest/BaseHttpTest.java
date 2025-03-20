package httpTest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import http.HttpTaskServer;
import http.adapter.DurationAdapter;
import http.adapter.LocalDateTimeAdapter;
import manager.InMemoryTaskManager;
import manager.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.io.IOException;
import java.net.http.HttpClient;
import java.time.Duration;
import java.time.LocalDateTime;

public abstract class BaseHttpTest {
    protected HttpTaskServer taskServer;
    protected TaskManager manager;
    protected HttpClient client;
    protected final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .setPrettyPrinting()
            .serializeNulls().create();

    @BeforeEach
    void setup() throws IOException {
        manager = new InMemoryTaskManager();
        taskServer = new HttpTaskServer(manager);
        taskServer.start();
        client = HttpClient.newHttpClient();
    }

    @AfterEach
    void shutDown() {
        taskServer.stop();
    }
}
