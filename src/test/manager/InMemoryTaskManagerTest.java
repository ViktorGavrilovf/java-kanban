package manager;

import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {
    private TaskManager taskManager;


    @BeforeEach
    void setup() {
        taskManager = Managers.getDefault();
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
        Task task1 = taskManager.createTask(new Task("Задача 1", "Описание задачи 1"));
        Task task2 = taskManager.createTask(new Task("Задача 2", "Описание задачи 2"));

        assertNotEquals(task1.getId(), task2.getId(), "ID задач должны быть уникальными.");
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
    void testSubtaskDeletionRemovesOldId() {
        Epic epic = taskManager.createEpic(new Epic("Эпик", "Описание эпика"));
        Subtask subtask = taskManager.createSubtask(new Subtask("Подазадача 1", "Описнаие подзадачи 1", epic.getId()));
        taskManager.deleteTaskById(subtask.getId());

        assertFalse(epic.getSubtasksId().contains(subtask.getId()), "ID удалённой подзадачи не должен оставаться в списке эпика.");
    }

    @Test
    void testEpicDoesNotContainDeletedSubtaskId() {
        Epic epic = taskManager.createEpic(new Epic("Эпик", "Описание эпика"));
        Subtask subtask1 = taskManager.createSubtask(new Subtask("Подазадача 1", "Описнаие подзадачи 1", epic.getId()));
        Subtask subtask2 = taskManager.createSubtask(new Subtask("Подазадача 1", "Описнаие подзадачи 1", epic.getId()));

        assertEquals(2, epic.getSubtasksId().size());

        taskManager.deleteTaskById(subtask1.getId());

        assertFalse(epic.getSubtasksId().contains(subtask1.getId()), "Эпик не должен содержать ID удалённой подзадачи");
        assertEquals(1, epic.getSubtasksId().size(), "После удаления должна остаться только одна подзадача");
        assertTrue(epic.getSubtasksId().contains(subtask2.getId()), "Эпик должен содержать ID второй подзадачи.");
    }

    @Test
    void testIdCannotBeChanged() {
        Task task = new Task("Task", "Description");
        Task createdTask = taskManager.createTask(task);

        int originalId = createdTask.getId();

        Task foundTask = taskManager.getTask(task.getId());
        assertNotNull(foundTask, "Задача должна быть доступна по старому ID");
        assertEquals(originalId, foundTask.getId(), "ID задачи не должно изменяться");
    }
}