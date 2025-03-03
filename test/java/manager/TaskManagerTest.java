package manager;

import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

abstract class TaskManagerTest<T extends TaskManager> {
    protected T manager;
    protected Task task1;
    protected Task task2;

    protected abstract T createTaskManager();

    @BeforeEach
    void setup() {
        manager = createTaskManager();
        task1 = manager.createTask(new Task(0, "Задача 1", "Описание задачи 1"));
        task2 = manager.createTask(new Task(0, "Задача 2", "Описание задачи 2"));
        manager.deleteAllTasks();
    }

    @Test
    void testTaskUniqueness() {
        assertNotEquals(task1.getId(), task2.getId(), "ID задач должны быть уникальными.");
    }

    @Test
    void testTaskImmutability() {
        task1.setStatus(TaskStatus.NEW);

        Task createTask = manager.createTask(new Task(0, "Задача 1", "Описание задачи 1"));

        assertEquals(task1.getTitle(), createTask.getTitle(), "Название задачи изменилось.");
        assertEquals(task1.getDescription(), createTask.getDescription(), "Описание задачи изменилось.");
        assertEquals(task1.getStatus(), createTask.getStatus(), "Статус задачи изменился.");
    }

    @Test
    void testSubtaskDeletionRemovesOldId() {
        Epic epic = manager.createEpic(new Epic(0, "Эпик", "Описание эпика"));
        Subtask subtask = manager.createSubtask(new Subtask(0, "Подазадача 1", "Описнаие подзадачи 1", epic.getId()));

        manager.deleteTaskById(subtask.getId());

        assertFalse(epic.getSubtasksId().contains(subtask.getId()), "ID удалённой подзадачи не должен оставаться в списке эпика.");
    }

    @Test
    void testEpicDoesNotContainDeletedSubtaskId() {
        Epic epic = manager.createEpic(new Epic(0, "Эпик", "Описание эпика"));
        Subtask subtask1 = manager.createSubtask(new Subtask(0, "Подазадача 1", "Описнаие подзадачи 1", epic.getId()));
        Subtask subtask2 = manager.createSubtask(new Subtask(0, "Подазадача 1", "Описнаие подзадачи 1", epic.getId()));

        assertEquals(2, epic.getSubtasksId().size());

        manager.deleteTaskById(subtask1.getId());

        assertFalse(epic.getSubtasksId().contains(subtask1.getId()), "Эпик не должен содержать ID удалённой подзадачи");
        assertEquals(1, epic.getSubtasksId().size(), "После удаления должна остаться только одна подзадача");
        assertTrue(epic.getSubtasksId().contains(subtask2.getId()), "Эпик должен содержать ID второй подзадачи.");
    }

    @Test
    void testIdCannotBeChanged() {
        Task createdTask = manager.createTask(new Task(0, "Задача 1", "Описание задачи 1"));
        int originalId = createdTask.getId();

        Task foundTask = manager.getTask(createdTask.getId());
        assertNotNull(foundTask, "Задача должна быть доступна по старому ID");
        assertEquals(originalId, foundTask.getId(), "ID задачи не должно изменяться");
    }

    @Test
    void shouldNotAllowOverlappingTasks() {
        task1 = manager.createTask(new Task(0, "Задача 1", "Описание 1",
                LocalDateTime.of(2025, 3, 10, 10, 0), Duration.ofMinutes(60)));

        task2 = new Task(0, "Задача 2", "Описание 2",
                LocalDateTime.of(2025, 3, 10, 10, 30), Duration.ofMinutes(30));
        assertThrows(IllegalArgumentException.class, () ->
                manager.createTask(task2), "Должно выбрасываться исключение при пересечении задач.");
    }

    @Test
    void shouldDeleteAllTasks() {
        Epic epic = manager.createEpic(new Epic(0, "Эпик", "Описание эпика"));
        Subtask subtask1 = new Subtask(0, "Подазадача 1", "Описание подзадачи 1", epic.getId());
        Subtask subtask2 = new Subtask(0, "Подазадача 1", "Описание подзадачи 1", epic.getId());
        manager.createSubtask(subtask1);
        manager.createSubtask(subtask2);

        manager.deleteAllTasks();
        manager.deleteAllSubtasks();
        manager.deleteAllEpics();

        assertTrue(manager.getAllTasks().isEmpty(), "Список задач должен быть пуст");
        assertTrue(manager.getAllEpics().isEmpty(), "Список эпиков должен быть пуст");
        assertTrue(manager.getAllSubtasks().isEmpty(), "Список подзадач должен быть пуст");
    }

    @Test
    void shouldUpdateSubtask() {
        Epic epic = manager.createEpic(new Epic(0, "Эпик", "Описание эпика"));
        Subtask subtask = manager.createSubtask(new Subtask(0, "Подзадача", "Описание подзадачи",
                LocalDateTime.of(2025, 3, 15, 14, 0), Duration.ofMinutes(45), epic.getId()));

        Subtask updatedSubtask = new Subtask(subtask.getId(), "Обновлённая подзадача", "Новое описание",
                LocalDateTime.of(2025, 3, 15, 15, 0), Duration.ofMinutes(30), epic.getId());

        manager.updateTask(updatedSubtask);
        Subtask result = manager.getSubtask(subtask.getId());

        assertNotNull(result, "Подзадача должна существовать после обновления");
        assertEquals("Обновлённая подзадача", result.getTitle(), "Название подзадачи должно обновиться");
        assertEquals("Новое описание", result.getDescription(), "Описание подзадачи должно обновиться");
        assertEquals(LocalDateTime.of(2025, 3, 15, 15, 0), result.getStartTime(),
                "Время старта должно обновиться");
        assertEquals(Duration.ofMinutes(30), result.getDuration(), "Продолжительность должна обновиться");
        assertEquals(subtask.getId(), result.getId(), "ID подзадачи не должен измениться");
    }

    @Test
    void shouldReturnTasksSortedByStartTime() {
        task1 = manager.createTask(new Task(0, "Задача 2 ", "Описание подзадачи",
                LocalDateTime.of(2025, 3, 15, 16, 15), Duration.ZERO));
        task2 = manager.createTask(new Task(0, "Задача 1", "Описание подзадачи",
                LocalDateTime.of(2025, 3, 15, 14, 0), Duration.ZERO));
        Task task3 = manager.createTask(new Task(0, "Задача 3", "Описание подзадачи",
                LocalDateTime.of(2025, 3, 15, 19, 10), Duration.ZERO));
        assertEquals(manager.getPrioritizedTasks(), List.of(task2, task1, task3),
                "Задачи должны быть в порядке возрастания startTime");
    }
}
