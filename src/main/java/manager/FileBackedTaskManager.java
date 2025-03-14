package manager;

import model.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File file;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    private void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write("id,type,name,status,description,startTime,duration,epicID\n");
            List<Task> taskList = new ArrayList<>();
            taskList.addAll(getAllTasks());
            taskList.addAll(getAllEpics());
            taskList.addAll(getAllSubtasks());

            for (Task task : taskList) {
                writer.write(taskToString(task) + "\n");
            }
        } catch (IOException exception) {
            throw new ManagerSaveException("Ошибка сохранения в файл: " + file.getName(), exception);
        }
    }

    @Override
    public Task createTask(Task task) {
        Task createTask = super.createTask(task);
        save();
        return createTask;
    }

    @Override
    public Epic createEpic(Epic epic) {
        Epic createEpic = super.createEpic(epic);
        save();
        return createEpic;
    }

    @Override
    public Subtask createSubtask(Subtask subtask) {
        Subtask createSubtask = super.createSubtask(subtask);
        save();
        return createSubtask;
    }

    @Override
    public void deleteTaskById(Integer id) {
        super.deleteTaskById(id);
        save();
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    @Override
    public void deleteAllEpics() {
        super.deleteAllEpics();
        save();
    }

    @Override
    public void deleteAllSubtasks() {
        super.deleteAllSubtasks();
        save();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager manager = new FileBackedTaskManager(file);

        if (file.exists()) {
            try {
                List<String> lines = Files.readAllLines(Path.of(file.getPath()));
                for (int i = 1; i < lines.size(); i++) {
                    String line = lines.get(i);
                    Task task = fromString(line);

                    switch (task.getType()) {
                        case TASK -> manager.tasks.put(task.getId(), task);
                        case SUBTASK -> manager.subtasks.put(task.getId(), (Subtask) task);
                        case EPIC -> manager.epics.put(task.getId(), (Epic) task);
                    }
                }

                manager.subtasks.values().forEach(subtask ->
                        manager.epics.get(subtask.getEpicId()).addSubtasksId(subtask.getId()));

                manager.getAllTasks().forEach(manager::addToPrioritizedTasks);
                manager.getAllSubtasks().forEach(manager::addToPrioritizedTasks);
                manager.epics.values().forEach(manager::updateTimeEpic);

            } catch (IOException exception) {
                throw new ManagerSaveException("Ошибка сохранения в файл: " + file.getName(), exception);
            }
        }
        return manager;
    }

    private String taskToString(Task task) {
        return String.format("%d,%s,%s,%s,%s,%s,%s,%s",
                task.getId(), task.getType(), task.getTitle(), task.getStatus(), task.getDescription(),
                task.getStartTime() != null ? task.getStartTime().format(formatter) : "",
                task.getDuration() != null ? task.getDuration().toMinutes() : "",
                task instanceof Subtask ? ((Subtask) task).getEpicId() : "");
    }

    private static Task fromString(String value) {
        Task task;
        String[] parts = value.split(",");
        int id = Integer.parseInt(parts[0]);
        TaskType type = TaskType.valueOf(parts[1]);
        String title = parts[2];
        TaskStatus status = TaskStatus.valueOf(parts[3]);
        String description = parts[4];

        String startTimePart = parts.length > 5 ? parts[5] : "";
        String durationPart = parts.length > 6 ? parts[6] : "";
        String epicIdPart = parts.length > 7 ? parts[7] : "";

        LocalDateTime startTime = !startTimePart.isEmpty() ? LocalDateTime.parse(startTimePart, formatter) : null;
        Duration duration = !durationPart.isEmpty() ? Duration.ofMinutes(Long.parseLong(durationPart)) : null;

        switch (type) {
            case TASK -> task = new Task(id, title, description, startTime, duration);
            case EPIC -> task = new Epic(id, title, description);
            case SUBTASK -> {
                int epicId = Integer.parseInt(epicIdPart);
                task = new Subtask(id, title, description, startTime, duration, epicId);
            }
            default -> throw new IllegalArgumentException("Неизвестный тип задачи: " + type);
        }
        task.setStatus(status);
        return task;
    }
}
