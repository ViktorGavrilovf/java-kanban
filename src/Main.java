import manager.TaskManager;
import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;

public class Main {
    public static void main(String[] args) {
        TaskManager manager = new TaskManager();

        // Создание двух задач
        Task task1 = manager.createTask(new Task("Задача 1", "Описание задачи 1"));
        Task task2 = manager.createTask(new Task("Задача 2", "Описание задачи 2"));

        // Создание эпика с двумя подзадачами
        Epic epic1 = manager.createEpic(new Epic("Эпик 1", "Эпик с двумя подзадачами"));
        Subtask subtask1 = manager.createSubtask(new Subtask("Подзадача 1-1", "Описание 1-1", epic1.getId()));
        Subtask subtask2 = manager.createSubtask(new Subtask("Подзадача 1-2", "Описание 1-2", epic1.getId()));

        // Создание эпика с одной подзадачей
        Epic epic2 = manager.createEpic(new Epic("Эпик 2", "Эпик с одной подзадачей"));
        Subtask subtask3 = manager.createSubtask(new Subtask("Подзадача 2-1", "Описание 2-1", epic2.getId()));

        // Печать всех задач, эпиков и подзадач
        System.out.println("Список всех задач:");
        System.out.println(manager.getAllTasks());

        System.out.println("Список всех эпиков:");
        System.out.println(manager.getAllEpics());

        System.out.println("Список всех подзадач:");
        System.out.println(manager.getAllSubtasks());

        // Изменение статусов задач и подзадач
        task1.setStatus(TaskStatus.DONE);
        subtask1.setStatus(TaskStatus.DONE);
        subtask2.setStatus(TaskStatus.IN_PROGRESS);
        subtask3.setStatus(TaskStatus.DONE);

        // Обновление объектов в менеджере
        manager.createTask(task1);
        manager.createSubtask(subtask1);
        manager.createSubtask(subtask2);
        manager.createSubtask(subtask3);

        // Печать обновлённых задач и эпиков
        System.out.println("Обновлённый статус задач:");
        System.out.println(manager.getAllTasks());

        System.out.println("Обновлённый статус эпиков:");
        System.out.println(manager.getAllEpics());

        // Удаление одной задачи и одного эпика
        System.out.println("Удаляем задачу с ID = 1 и эпик с ID = 2");
        manager.deleteAllTasks(); // Удаление всех задач
        manager.deleteAllEpics(); // Удаление всех эпиков

        // Печать оставшихся задач и эпиков
        System.out.println("Список задач после удаления:");
        System.out.println(manager.getAllTasks());

        System.out.println("Список эпиков после удаления:");
        System.out.println(manager.getAllEpics());
    }
}
