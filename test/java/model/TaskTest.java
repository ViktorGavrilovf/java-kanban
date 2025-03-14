package model;

import manager.TaskManager;
import manager.Managers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {
    private TaskManager taskManager;
    private Task task;

    @BeforeEach
    void setup() {
        taskManager = Managers.getDefault();
        task = taskManager.createTask(new Task(0, "Задача 1", "Описание задачи 1"));
    }

    @Test
    void testCreateTask() {
        assertNotNull(task, "Задача не должна быть null.");
        assertEquals(TaskStatus.NEW, task.getStatus(), "Начальный статус задачи должен быть NEW.");
    }

    @Test
    void testChangeTaskStatus() {
        task = taskManager.createTask(new Task(0, "Задача 1", "Описание задачи 1"));
        task.setStatus(TaskStatus.IN_PROGRESS);
        taskManager.createTask(new Task(task.getId(), task.getTitle(), task.getDescription()));

        Task updatedTask = taskManager.getTask(task.getId());
        assertEquals(TaskStatus.IN_PROGRESS, updatedTask.getStatus(),
                "Статус задачи должен измениться на IN_PROGRESS.");

        task.setStatus(TaskStatus.DONE);
        taskManager.createTask(new Task(task.getId(), task.getTitle(), task.getDescription()));

        updatedTask = taskManager.getTask(task.getId());
        assertEquals(TaskStatus.DONE, updatedTask.getStatus(), "Статус задачи должен измениться на DONE.");
    }
}