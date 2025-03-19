package httpTest;

import model.Epic;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class HttpSubtasksHandlerTest extends BaseHttpTest {
    private Epic epic;
    private Subtask subtask1;
    private Subtask subtask2;

    @BeforeEach
    void setup() throws IOException {
        super.setup();
        epic = manager.createEpic(new Epic(0, "Эпик", "Описание эпика"));
    }

    @Test
    void shouldCreateSubtask() throws IOException, InterruptedException {
        String json = gson.toJson(new Subtask(0, "Подзадача", "Описание подзадачи", epic.getId()));

        URI uri = URI.create("http://localhost:8080/subtasks/");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode(), "Подзадача не была создана");
        assertEquals(1, manager.getAllEpics().size(), "Подзадача не добавлена в менеджер");
    }

    @Test
    void shouldReturnAllSubtasks() throws IOException, InterruptedException {
        subtask1 = manager.createSubtask(new Subtask(0, "Подзадача 1", "Описание подзадачи 1", epic.getId()));
        subtask2 = manager.createSubtask(new Subtask(0, "Подзадача 2", "Описание подзадачи 2", epic.getId()));
        URI uri = URI.create("http://localhost:8080/subtasks/");
        HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Ошибка при получении подзадач");
    }

    @Test
    void shouldGetTaskById() throws IOException, InterruptedException {
        subtask1 = manager.createSubtask(new Subtask(0, "Подзадача 1", "Описание подзадачи 1", epic.getId()));
        URI uri = URI.create("http://localhost:8080/subtasks/" + subtask1.getId());
        HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Ошибка получения задачи по ID");
    }

    @Test
    void shouldUpdateSubtaskById() throws IOException, InterruptedException {
        Subtask subtask = manager.createSubtask(new Subtask(0, "Подзадача 1", "Описание подзадачи 1",
                LocalDateTime.of(2021, 3, 16, 12, 0), Duration.ofMinutes(45), epic.getId()));
        subtask.setTitle("Другой заголовок");

        String updatedTaskJson = gson.toJson(subtask);
        URI uri = URI.create("http://localhost:8080/subtasks/" + subtask.getId());
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .POST(HttpRequest.BodyPublishers.ofString(updatedTaskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Ошибка при обновлении задачи");

        Task updatedTask = manager.getSubtask(subtask.getId());
        assertNotNull(updatedTask, "Обновлённая задача не найдена");
        assertEquals("Другой заголовок", updatedTask.getTitle(), "Заголовок задачи не обновился");
    }

    @Test
    void shouldDeleteAllSubtasks() throws IOException, InterruptedException {
        URI uri = URI.create("http://localhost:8080/subtasks/");
        HttpRequest request = HttpRequest.newBuilder().uri(uri).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Ошибка при удалении всех подзадач");
        assertEquals(0, manager.getAllSubtasks().size(), "После удаления список подзадач должен быть пуст");
    }

    @Test
    void shouldDeleteTaskById() throws IOException, InterruptedException {
        subtask1 = manager.createSubtask(new Subtask(0, "Подзадача 1", "Описание подзадачи 1", epic.getId()));

        URI uri = URI.create("http://localhost:8080/subtasks/" + subtask1.getId());
        HttpRequest request = HttpRequest.newBuilder().uri(uri).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Ошибка при удалении задачи");
        assertEquals(0, manager.getAllTasks().size(), "Список задач должен быть пуст");
    }
}
