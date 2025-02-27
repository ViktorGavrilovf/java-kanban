package manager;

import model.Epic;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class FileBackedTaskManagerTest {

    private File tempFile;
    private FileBackedTaskManager manager;

    @BeforeEach
    void setup() throws IOException {
        tempFile = File.createTempFile("taskTest", ".csv");
        manager = new FileBackedTaskManager(tempFile);
    }

    @Test
    void testEmptyFile() {
        FileBackedTaskManager loadManager = FileBackedTaskManager.loadFromFile(tempFile);

        assertTrue(loadManager.getAllTasks().isEmpty(), "Список задач должен быть пуст");
        assertTrue(loadManager.getAllEpics().isEmpty(), "Список эпиков должен быть пуст");
        assertTrue(loadManager.getAllSubtasks().isEmpty(), "Список подзадач должен быть пуст");
    }

    @Test
    void saveAndLoadTasksTest() {
        Task task = manager.createTask(new Task(0, "Задача", "Описание"));
        Epic epic = manager.createEpic(new Epic(0, "Эпик", "Описание эпика"));
        Subtask subtask = manager.createSubtask(new Subtask(0, "Подзадача", "Описание подзадачи", epic.getId()));

        FileBackedTaskManager loadManager = FileBackedTaskManager.loadFromFile(tempFile);

        assertEquals(1, loadManager.getAllTasks().size(),
                "Количество загруженных задач должно быть 1");
        assertEquals(1, loadManager.getAllEpics().size(),
                "Количество загруженных эпиков должно быть 1");
        assertEquals(1, loadManager.getAllSubtasks().size(),
                "Количество загруженных подзадач должно быть 1");

        assertEquals(task.getTitle(), loadManager.getTask(task.getId()).getTitle(),
                "Загруженная задача должна совпадать");
        assertEquals(epic.getTitle(), loadManager.getEpic(epic.getId()).getTitle(),
                "Загруженный эпик должна совпадать");
        assertEquals(subtask.getTitle(), loadManager.getSubtask(subtask.getId()).getTitle(),
                "Загруженная подзадача должна совпадать");
    }
}
