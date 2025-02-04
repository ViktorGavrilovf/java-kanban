package model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class TaskTest {

    @Test
    void taskTestEqualsById() {
        Task task1 = new Task("Задача 1", "Описание 1");
        Task task2 = new Task("Задача 2", "Описание 2");

        task1.setId(1);
        task2.setId(1);

        assertEquals(task1, task2, "Задачи равны");
    }

    @Test
    void taskTestNotEqualsById() {
        Task task1 = new Task("Задача 1", "Описание 1");
        Task task2 = new Task("Задача 2", "Описание 2");

        task1.setId(1);
        task2.setId(2);

        assertNotEquals(task1, task2, "Задачи не равны");
    }
}