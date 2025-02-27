package manager;

import model.Task;
import model.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class InMemoryHistoryManagerTest {

    private HistoryManager historyManager;
    private Task task1;
    private Task task2;
    private Task task3;

    @BeforeEach
    void setup() {
        TaskManager taskManager = Managers.getDefault();
        historyManager = Managers.getDefaultHistory();

        task1 = taskManager.createTask(new Task(0, "Задача 1", "Описание задачи 1"));
        task2 = taskManager.createTask(new Task(0, "Задача 2", "Описание задачи 2"));
        task3 = taskManager.createTask(new Task(0, "Задача 3", "Описание задачи 3"));
    }

    @Test
    void testAddInHistory() {
        historyManager.add(task1);
        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size(), "История должна содержать 1 элемент");
        assertEquals(task1, history.get(0), "Задача в истории не совпадает.");
    }

    @Test
    void testHistoryStoresPreviousVersion() {
        task1.setStatus(TaskStatus.NEW);
        historyManager.add(task1);

        // Меняем данные задачи после внесения в историю
        task1.setTitle("Задача 2");
        task1.setDescription("Описание задачи 2");
        task1.setStatus(TaskStatus.DONE);

        List<Task> history = historyManager.getHistory();

        assertEquals(task1, history.get(0), "Данные задачи в истории должны оставаться неизменным.");
    }

    @Test
    void testHistoryRemovesDuplicates() {
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task1); // Повторно добавляем задачу 1.
        List<Task> history = historyManager.getHistory();
        assertEquals(2, history.size(), "История не должна содержать дубликаты");
        assertEquals(task1, history.get(1), "задача 1 должна быть перемещена в конец.");
    }

    @Test
    void testRemoveTaskFromHistory() {
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);

        historyManager.remove(task1.getId());

        List<Task> history = historyManager.getHistory();

        assertEquals(2, history.size(), "История должна содержать 2 элемента после удаления");
        assertNotEquals(task1, history.get(0), "Удалённая задача всё ещё присутствует в истории");
        assertEquals(task2, history.get(0));
        assertEquals(task3, history.get(1));
    }
}
