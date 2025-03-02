import manager.Managers;
import manager.TaskManager;
import model.Epic;
import model.Subtask;
import model.TaskStatus;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        // Создаем эпик напрямую
        Epic epic = new Epic(1, "Эпик тестовый", "Описание эпика");

        // Создаем подзадачи напрямую (не через менеджер)
        Subtask subtask1 = new Subtask(2, "Подзадача 1", "Описание подзадачи 1",
                LocalDateTime.of(2025, 3, 1, 10, 0), Duration.ofMinutes(60), epic.getId());

        Subtask subtask2 = new Subtask(3, "Подзадача 2", "Описание подзадачи 2",
                LocalDateTime.of(2025, 3, 1, 12, 0), Duration.ofMinutes(90), epic.getId());

        // Добавляем ID подзадач в эпик (имитируем ручное добавление, как будто без менеджера)
        epic.addSubtasksId(subtask1.getId());
        epic.addSubtasksId(subtask2.getId());

        // Ручной вызов пересчёта времени эпика (как будто мы не через менеджер работаем)
        epic.updateEpicTime(List.of(subtask1, subtask2));

        // Вывод результатов
        System.out.println("Эпик после добавления подзадач:");
        System.out.println(epic);

        System.out.println("Подзадача 1: " + subtask1);
        System.out.println("Подзадача 2: " + subtask2);

        // Проверяем итоговые данные
        System.out.println("Ожидаемый startTime: 2025-03-01T10:00");
        System.out.println("Фактический startTime: " + epic.getStartTime());

        System.out.println("Ожидаемый endTime: 2025-03-01T13:30");
        System.out.println("Фактический endTime: " + epic.getEndTime());

        System.out.println("Ожидаемая продолжительность: 150 минут");
        System.out.println("Фактическая продолжительность: " + epic.getDuration().toMinutes() + " минут");
    }
}
