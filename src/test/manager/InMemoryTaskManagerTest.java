package manager;

import model.Task;
import model.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {
    private TaskManager taskManager;
    private HistoryManager historyManager;

    @BeforeEach
    void setup() {
        taskManager = Managers.getDefault();
        historyManager = Managers.getDefaultHistory();
    }

    @Test
    void testAddAndFindTaskById() {
        Task task = new Task("Задача 1", "Описание задачи 1");
        Task createTask = taskManager.createTask(task);

        Task taskFound = taskManager.getTask(createTask.getId());

        assertNotNull(taskFound, "Задача должна быть найдена.");
        assertEquals(task, taskFound, "Созданная и найденная задачи должны совпадать.");
    }

    @Test
    void testTaskUniqueness() {
        Task task1 = new Task("Задача 1", "Описание задачи 1");
        Task task2 = new Task("Задача 2", "Описание задачи 2");

        Task createTask = taskManager.createTask(task1);
        task2.setId(123);

        assertNotEquals(createTask.getId(), task2.getId(), "ID задач должны быть уникальными.");
    }

    @Test
    void testTaskImmutability() {
        Task task = new Task("Задача 1", "Описание задачи 1");
        task.setStatus(TaskStatus.NEW);

        Task createTask = taskManager.createTask(task);

        assertEquals(task.getTitle(), createTask.getTitle(), "Название задачи изменилось.");
        assertEquals(task.getDescription(), createTask.getDescription(), "Описание задачи изменилось.");
        assertEquals(task.getStatus(), createTask.getStatus(), "Статус задачи изменился.");
    }

    @Test
    void testAddInHistory() {
        Task task = new Task("Задача 1", "Описание задачи 1");
        historyManager.addHistory(task);
        List<Task> history = historyManager.getHistory();
        assertNotNull(history, "История не пустая.");
        assertEquals(1, history.size(), "История не пустая.");
    }

    @Test
    void testHistoryStoresPreviousVersion() {
        Task task = new Task("Задача 1", "Описание задачи 1");
        task.setId(1);
        task.setStatus(TaskStatus.NEW);

        historyManager.addHistory(task);

        // Меняем данные задачи после внесения в историю
        task.setTitle("Задача 2");
        task.setDescription("Описание задачи 2");
        task.setStatus(TaskStatus.DONE);
        task.setId(12);

        List<Task> history = historyManager.getHistory();

        assertEquals(task, history.get(0), "Данные задачи в истории должны оставаться неизменным.");
    }

    @Test
    void testReplaceLastTaskInHistory() { // Тест удаления старой задачи в истории при превышении размера списка
        for (int i = 0; i < 10; i++) {
            historyManager.addHistory(new Task("Задача " + i, "Описание задачи " + i));
        }
        Task task = new Task("Задача 15", "Описание задачи 15");
        historyManager.addHistory(task);
        List<Task> history = historyManager.getHistory();

        assertEquals(task.getTitle(), history.get(9).getTitle(), "Задачи не равны");
        assertFalse(history.size() > 10, "Размер истории больше 10 элементов");
    }
}