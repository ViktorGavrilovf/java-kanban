package model;

import manager.InMemoryTaskManager;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotEquals;

class SubtaskTest {
    @Test
    void testSubtaskCannotBeItsOwnEpic() {
        InMemoryTaskManager manager = new InMemoryTaskManager();

        Epic epic = manager.createEpic(new Epic("Эпик 1", "Эпик с одной подзадачей"));
        Subtask subtask = manager.createSubtask(new Subtask("Подзадача 1-1",
                "Описание 1-1", epic.getId()));

        assertNotEquals(subtask.getId(), subtask.getEpicId(),
                "Подзадача не может быть своим эпиком.");
    }
}