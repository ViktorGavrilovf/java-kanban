package model;

import manager.Managers;
import manager.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

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
}