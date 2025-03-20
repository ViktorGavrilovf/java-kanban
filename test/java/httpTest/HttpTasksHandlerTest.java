package httpTest;

import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class HttpTasksHandlerTest extends BaseHttpTest {

    private Task task1;
    private Task task2;

    @BeforeEach
    void setup() throws IOException {
        super.setup();
        task1 = new Task(0, "Задача 1", "Первая задача",
                LocalDateTime.of(2024, 3, 16, 9, 0), Duration.ofMinutes(60));
        task2 = new Task(0, "Задача 2", "Вторая задача",
                LocalDateTime.of(2024, 3, 16, 11, 0), Duration.ofMinutes(90));
    }

    @Test
    void shouldCreateTask() throws IOException, InterruptedException {
        Task task = new Task(0, "Тестовая задача", "Описание задачи",
                LocalDateTime.now(), Duration.ofMinutes(30));
        String taskJson = gson.toJson(task);

        URI uri = URI.create("http://localhost:8080/tasks/");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode(), "Задача не была создана");

        List<Task> tasks = manager.getAllTasks();
        assertEquals(1, tasks.size(), "Задача не добавлена в менеджер");
        assertEquals("Тестовая задача", tasks.get(0).getTitle(), "Некорректный заголовок задачи");
    }

    @Test
    void shouldReturnTasksList() throws IOException, InterruptedException {
        manager.createTask(task1);
        manager.createTask(task2);

        URI uri = URI.create("http://localhost:8080/tasks/");
        HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Ошибка при получении списка задач");

        String responseBody = response.body();
        assertTrue(responseBody.contains("Задача 1"), "Не найдена первая задача");
        assertTrue(responseBody.contains("Задача 2"), "Не найдена вторая задача");
    }

    @Test
    void shouldGetTaskById() throws IOException, InterruptedException {
        manager.createTask(task1);

        URI uri = URI.create("http://localhost:8080/tasks/" + task1.getId());
        HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Ошибка получения задачи по ID");
    }

    @Test
    void shouldDeleteTaskById() throws IOException, InterruptedException {
        manager.createTask(task1);

        URI uri = URI.create("http://localhost:8080/tasks/" + task1.getId());
        HttpRequest request = HttpRequest.newBuilder().uri(uri).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Ошибка при удалении задачи");
        assertEquals(0, manager.getAllTasks().size(), "Список задач должен быть пуст");
    }

    @Test
    void shouldDeleteAllTasks() throws IOException, InterruptedException {
        manager.createTask(task1);
        manager.createTask(task2);

        URI uri = URI.create("http://localhost:8080/tasks/");
        HttpRequest request = HttpRequest.newBuilder().uri(uri).DELETE().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Ошибка при удалении задач");
        assertEquals(0, manager.getAllTasks().size(), "Список задач должен быть пуст");
    }

    @Test
    void shouldUpdateTaskById() throws IOException, InterruptedException {
        Task createdTask = manager.createTask(task1);
        int taskId = createdTask.getId();

        createdTask.setTitle("Обновлённая задача");

        String updatedTaskJson = gson.toJson(createdTask);
        URI uri = URI.create("http://localhost:8080/tasks/" + taskId);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .POST(HttpRequest.BodyPublishers.ofString(updatedTaskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode(), "Ошибка при обновлении задачи");

        Task updatedTask = manager.getTask(taskId);
        assertNotNull(updatedTask, "Обновлённая задача не найдена");
        assertEquals("Обновлённая задача", updatedTask.getTitle(), "Заголовок задачи не обновился");
    }

    @Test
    void shouldNotCreateOverlappingTask() throws IOException, InterruptedException {
        manager.createTask(task1);
        Task overlappingTask = new Task(0, "Пересекающаяся задача", "Должна вызвать ошибку",
                task1.getStartTime().plusMinutes(30), Duration.ofMinutes(60));

        String taskJson = gson.toJson(overlappingTask);
        URI uri = URI.create("http://localhost:8080/tasks/");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(406, response.statusCode(), "Сервер должен отклонить пересекающуюся задачу");
        assertEquals(1, manager.getAllTasks().size(), "Должна остаться только первая задача");
    }
}
