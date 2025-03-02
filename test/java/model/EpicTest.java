package model;

import manager.Managers;
import manager.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {
    private TaskManager taskManager;
    private Epic epic;
    private Subtask subtask1;
    private Subtask subtask2;

    @BeforeEach
    void setup() {
        taskManager = Managers.getDefault();
        epic = taskManager.createEpic(new Epic(0, "Эпик", "Описание эпика"));
    }

    @Test
    void testEpicCannotContainItselfAsSubtask() {
        subtask1 = taskManager.createSubtask(new Subtask(0, "Подзадача 1-1", "Описание 1-1", epic.getId()));
        epic.addSubtasksId(epic.getId());
        assertFalse(epic.getSubtasksId().contains(epic.getId()),
                "Эпик не должен содержать самого себя как подзадачу.");
    }

    @Test
    void testEpicStatusUpdatesWithSubtasks() {
        subtask1 = taskManager.createSubtask(new Subtask(0, "Подзадача 1", "Описание 1", epic.getId()));
        subtask2 = taskManager.createSubtask(new Subtask(0, "Подзадача 2", "Описание 2", epic.getId()));

        assertEquals(TaskStatus.NEW, epic.getStatus(), "Эпик должен быть NEW, если все подзадачи NEW.");

        subtask1.setStatus(TaskStatus.IN_PROGRESS);
        taskManager.updateTask(subtask1);
        assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus(),
                "Эпик должен быть IN_PROGRESS, если хотя бы одна подзадача IN_PROGRESS.");

        subtask2.setStatus(TaskStatus.DONE);
        taskManager.updateTask(subtask2);
        assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus(),
                "Эпик должен оставаться IN_PROGRESS, если хотя бы одна подзадача не DONE.");

        subtask1.setStatus(TaskStatus.DONE);
        taskManager.updateTask(subtask1);
        assertEquals(TaskStatus.DONE, epic.getStatus(),
                "Эпик должен быть DONE, если все подзадачи DONE.");
    }

    @Test
    void shouldHaveNullTimeWhenNoSubtasks() {
        assertNull(epic.getStartTime(), "Время старта должно быть null");
        assertEquals(Duration.ZERO, epic.getDuration(), "Продолжительность должна быть 0");
        assertNull(epic.getEndTime(), "Время окончания должно быть null");
    }

    @Test
    void shouldCalculateTimeFromSubtasks() {
        Subtask subtask1 = taskManager.createSubtask(new Subtask(0, "Задача 1", "Описание 1",
                LocalDateTime.of(2025, 3, 1, 10, 0), Duration.ofMinutes(60), epic.getId()));
        Subtask subtask2 = taskManager.createSubtask(new Subtask(0, "Задача 2", "Описание 2",
                LocalDateTime.of(2025, 3, 1, 12, 0), Duration.ofMinutes(90), epic.getId()));

        assertEquals(epic.getStartTime(), subtask1.getStartTime(),
                "Время старта эпика должно совпадать с самой ранней подзадачей");

        assertEquals(Duration.ofMinutes(150), epic.getDuration(),
                "Продолжительность эпика должна быть суммой всех подзадач");

        assertEquals(subtask2.getEndTime(), epic.getEndTime(),
                "Время окончания должно совпадать с концом последней подзадачи");
    }

    @Test
    void shouldUpdateTimeAfterSubtaskDeletion() {
        Subtask sub1 = taskManager.createSubtask(new Subtask(0, "Задача 1", "Описание 1",
                LocalDateTime.of(2025, 3, 1, 10, 0), Duration.ofMinutes(60), epic.getId()));
        Subtask sub2 = taskManager.createSubtask(new Subtask(0, "Задача 2", "Описание 2",
                LocalDateTime.of(2025, 3, 1, 12, 0), Duration.ofMinutes(90), epic.getId()));

        // Удаляем первую подзадачу
        taskManager.deleteTaskById(sub1.getId());

        // Получаем актуальную версию эпика из менеджера
        Epic actualEpic = taskManager.getEpic(epic.getId());

        assertEquals(LocalDateTime.of(2025, 3, 1, 12, 0), actualEpic.getStartTime(),
                "Время старта должно обновиться после удаления первой подзадачи");
        assertEquals(Duration.ofMinutes(90), actualEpic.getDuration(),
                "Продолжительность должна обновиться после удаления подзадачи");
        assertEquals(LocalDateTime.of(2025, 3, 1, 13, 30), actualEpic.getEndTime(),
                "Время окончания должно обновиться после удаления подзадачи");
    }


    @Test
    void shouldUpdateStatusBasedOnSubtaskStatus() {
        Subtask sub1 = taskManager.createSubtask(new Subtask(0, "Задача 1", "Описание 1", epic.getId()));
        Subtask sub2 = taskManager.createSubtask(new Subtask(0, "Задача 2", "Описание 2", epic.getId()));

        assertEquals(TaskStatus.NEW, epic.getStatus(),
                "Эпик должен быть в статусе NEW, когда все подзадачи NEW");

        sub1.setStatus(TaskStatus.IN_PROGRESS);
        taskManager.updateTask(sub1);

        assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus(),
                "Эпик должен быть IN_PROGRESS, если хотя бы одна подзадача IN_PROGRESS");

        sub1.setStatus(TaskStatus.DONE);
        sub2.setStatus(TaskStatus.DONE);
        taskManager.updateTask(sub1);
        taskManager.updateTask(sub2);

        assertEquals(TaskStatus.DONE, epic.getStatus(),
                "Эпик должен быть DONE, когда все подзадачи DONE");
    }
}