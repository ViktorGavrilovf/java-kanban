package httpTest;

import model.Epic;
import model.Subtask;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class HttpEpicsHandlerTest extends BaseHttpTest {
    private Epic epic;

    @Test
    void shouldCreateEpic() throws IOException, InterruptedException {
        String json = gson.toJson(new Epic(0, "Тестовый эпик", "Описание эпика"));

        URI uri = URI.create("http://localhost:8080/epics/");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode(), "Эпик не был создан");
        assertEquals(1, manager.getAllEpics().size(), "Эпик не добавлен в менеджер");
    }

    @Test
    void shouldReturnEpicSubtasks() throws IOException, InterruptedException {
        epic = manager.createEpic(new Epic(0, "Тестовый эпик", "Описание эпика"));
        Subtask subtask = new Subtask(0, "Подзадача", "Описание подзадачи",
                LocalDateTime.of(2024, 3, 16, 12, 0), Duration.ofMinutes(45), epic.getId());
        manager.createSubtask(subtask);

        URI uri = URI.create("http://localhost:8080/epics/" + epic.getId() + "/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Ошибка при получении подзадач эпика");

        String responseBody = response.body();
        assertTrue(responseBody.contains("Подзадача"), "Не найдена подзадача эпика");
    }

    @Test
    void shouldReturnAllEpics() throws IOException, InterruptedException {
        manager.createEpic(new Epic(0, "Эпик 1", "Описание 1"));
        manager.createEpic(new Epic(0, "Эпик 2", "Описание 2"));

        URI uri = URI.create("http://localhost:8080/epics/");
        HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Ошибка при получении списка эпиков");

        String responseBody = response.body();
        assertTrue(responseBody.contains("Эпик 1"), "Не найден эпик 1");
        assertTrue(responseBody.contains("Эпик 2"), "Не найден эпик 2");
    }

    @Test
    void shouldGetEpicById() throws IOException, InterruptedException {
        epic = manager.createEpic(new Epic(0, "Тестовый эпик", "Описание эпика"));

        URI uri = URI.create("http://localhost:8080/epics/" + epic.getId());
        HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Ошибка при получении эпика по ID");
    }

    @Test
    void shouldDeleteEpicById() throws IOException, InterruptedException {
        epic = manager.createEpic(new Epic(0, "Тестовый эпик", "Описание эпика"));

        URI uri = URI.create("http://localhost:8080/epics/" + epic.getId());
        HttpRequest request = HttpRequest.newBuilder().uri(uri).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Ошибка при удалении задачи");
        assertEquals(0, manager.getAllEpics().size(), "Список задач должен быть пуст");
    }

    @Test
    void shouldDeleteAllEpics() throws IOException, InterruptedException {
        manager.createEpic(new Epic(0, "Эпик 1", "Описание 1"));
        manager.createEpic(new Epic(0, "Эпик 2", "Описание 2"));

        URI uri = URI.create("http://localhost:8080/epics/");
        HttpRequest request = HttpRequest.newBuilder().uri(uri).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Ошибка при удалении всех эпиков");
        assertEquals(0, manager.getAllEpics().size(), "Эпики не были удалены");
    }
}
