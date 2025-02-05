import manager.InMemoryTaskManager;
import model.Epic;
import model.Subtask;
import model.Task;

public class Main {
    public static void main(String[] args) {
        InMemoryTaskManager manager = new InMemoryTaskManager();

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

        System.out.println("Вызов getTask:");
        manager.getTask(task1.getId());
        manager.getTask(task2.getId());
        printAllTasks(manager);

        System.out.println("Вызов getEpic:");
        manager.getEpic(epic1.getId());
        printAllTasks(manager);

        System.out.println("Вызов getSubtask:");
        manager.getSubtask(subtask1.getId());
        manager.getSubtask(subtask2.getId());
        manager.getSubtask(subtask3.getId());
        printAllTasks(manager);

        System.out.println("Вызов getEpic для второго эпика:");
        manager.getEpic(epic2.getId());
        printAllTasks(manager);
    }

    private static void printAllTasks(InMemoryTaskManager manager) {
        System.out.println("Задачи:");
        for (Task task : manager.getAllTasks()) {
            System.out.println(task);
        }
        System.out.println("Эпики:");
        for (Task epic : manager.getAllEpics()) {
            System.out.println(epic);

            for (Subtask subtask : manager.getAllSubtasks()) {
                if (subtask.getEpicId() == epic.getId()) {
                    System.out.println("--> " + subtask);
                }
            }
        }

        System.out.println("История:");
        for (Task task : manager.getHistory()) {
            System.out.println(task);
        }
    }
}
