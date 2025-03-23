package httpTest;

import model.Task;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class HttpHistoryHandlerTest extends BaseHttpTest {

    @Test
    void shouldReturnEmptyHistory() throws IOException, InterruptedException {
        URI uri = URI.create("http://localhost:8080/history");
        HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Ошибка при получении пустой истории");
        assertEquals("[]", response.body(), "История должна быть пустой");
    }

    @Test
    void shouldAddTaskToHistory() throws IOException, InterruptedException {
        Task task = manager.createTask(new Task(0, "Задача", "Описание задачи"));
        manager.getTask(task.getId());

        URI uri = URI.create("http://localhost:8080/history");
        HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Ошибка при получении истории");
        assertTrue(response.body().contains("Задача"), "Задача не добавилась в историю");
    }
}
