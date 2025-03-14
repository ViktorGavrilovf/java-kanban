import manager.FileBackedTaskManager;
import model.Epic;
import model.Subtask;
import model.Task;

import java.io.File;

public class Main {
    public static void main(String[] args) {
        File file = new File("tasks.csv");

        // Создаём менеджер задач с сохранением в файл
        FileBackedTaskManager manager = new FileBackedTaskManager(file);

        // Добавляем задачи, эпики и подзадачи
        Task task1 = manager.createTask(new Task(0, "Купить продукты", "Купить молоко и хлеб"));
        Epic epic1 = manager.createEpic(new Epic(0, "Сделать ремонт", "Ремонт в ванной"));
        Subtask subtask1 = manager.createSubtask(
                new Subtask(0, "Покрасить стены", "Выбрать цвет и покрасить", epic1.getId()));

        // Выводим созданные задачи
        System.out.println("Задачи до загрузки из файла:");
        System.out.println("Задачи: " + manager.getAllTasks());
        System.out.println("Эпики: " + manager.getAllEpics());
        System.out.println("Подзадачи: " + manager.getAllSubtasks());

        // Загружаем менеджер из файла
        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(file);

        // Выводим загруженные задачи
        System.out.println("\nЗадачи после загрузки из файла:");
        System.out.println("Задачи: " + loadedManager.getAllTasks());
        System.out.println("Эпики: " + loadedManager.getAllEpics());
        System.out.println("Подзадачи: " + loadedManager.getAllSubtasks());

        // Проверяем, что подзадача сохранила связь с эпиком
        System.out.println("\nПроверяем связь подзадачи с эпиком:");
        System.out.println("Подзадача: " + loadedManager.getSubtask(subtask1.getId()));
        System.out.println("Эпик, к которому она принадлежит: " + loadedManager.getEpic(epic1.getId()));
    }
}
