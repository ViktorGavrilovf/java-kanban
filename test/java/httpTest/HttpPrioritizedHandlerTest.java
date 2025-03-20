package httpTest;

import com.google.gson.reflect.TypeToken;
import model.Task;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class HttpPrioritizedHandlerTest extends BaseHttpTest {

    @Test
    void shouldReturnPrioritizedTasks() throws IOException, InterruptedException {
        Task task3 = manager.createTask(new Task(0, "Третья задача", "Описание",
                LocalDateTime.of(2024, 3, 3, 8, 0), Duration.ZERO));
        Task task2 = manager.createTask(new Task(0, "Вторая задача", "Описание",
                LocalDateTime.of(2024, 3, 2, 8, 0), Duration.ZERO));
        Task task4 = manager.createTask(new Task(0, "Четвёртая задача", "Описание",
                LocalDateTime.of(2024, 3, 4, 8, 0), Duration.ZERO));
        Task task1 = manager.createTask(new Task(0, "Первая задача", "Описание",
                LocalDateTime.of(2024, 3, 1, 8, 0), Duration.ZERO));

        URI uri = URI.create("http://localhost:8080/prioritized/");
        HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Ошибка при получении приоритетных задач");
        assertTrue(response.body().contains("Первая задача"), "задача отсутствует в списке");

        List<Task> tasksFromResponse = gson.fromJson(response.body(), new TypeToken<List<Task>>() {
        }.getType());
        assertEquals(List.of(task1, task2, task3, task4), tasksFromResponse, "Задачи не отсортированы");

    }
}
