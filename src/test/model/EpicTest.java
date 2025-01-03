package model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {
    @Test
    public void testTaskEqualsById() {
        Epic epic1 = new Epic("Эпик 1", "Описание 1");
        Epic epic2 = new Epic("Эпик 2", "Описание 2");

        epic1.setId(1);
        epic2.setId(1);

        assertEquals(epic1, epic2, "Эпики равны");
    }

    @Test
    public void testEpicCannotContainItselfAsSubtask() {
        Epic epic = new Epic("Epic", "Description");
        epic.setId(1);
        epic.addSubtasksId(epic.getId());
        assertFalse(epic.getSubtasksId().contains(epic.getId()), "Эпик не должен содержать самого себя как подзадачу.");
    }
}