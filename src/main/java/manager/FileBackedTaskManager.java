package manager;

import model.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File file;

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    private void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write("id,type,name,status,description,epic\n");
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
    public void updateTask(Subtask subtask) {
        super.updateTask(subtask);
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

                    if (task instanceof Epic) {
                        manager.epics.put(task.getId(), (Epic) task);
                    } else if (task instanceof Subtask) {
                        manager.subtasks.put(task.getId(), (Subtask) task);
                    } else {
                        manager.tasks.put(task.getId(), task);
                    }
                }

                for (Subtask subtask : manager.subtasks.values()) {
                    Epic epic = manager.epics.get(subtask.getEpicId());
                    epic.addSubtasksId(subtask.getId());
                }
            } catch (IOException exception) {
                throw new ManagerSaveException("Ошибка сохранения в файл: " + file.getName(), exception);
            }
        }
        return manager;
    }

    private String taskToString(Task task) {
        String taskType = String.format("%d,%s,%s,%s,%s",
                task.getId(), task.getType(), task.getTitle(), task.getStatus(), task.getDescription());
        if (task instanceof Subtask) taskType += "," + ((Subtask) task).getEpicId();
        return taskType;
    }

    private static Task fromString(String value) {
        Task task;
        String[] parts = value.split(",");
        int id = Integer.parseInt(parts[0]);
        TaskType type = TaskType.valueOf(parts[1]);
        String title = parts[2];
        TaskStatus status = TaskStatus.valueOf(parts[3]);
        String description = parts[4];

        if (type.equals(TaskType.TASK)) {
            task = new Task(id, title, description);
        } else if (type.equals(TaskType.EPIC)) {
            task = new Epic(id, title, description);
        } else {
            int epicId = Integer.parseInt(parts[5]);
            task = new Subtask(id, title, description, epicId);
        }
        task.setStatus(status);
        return task;
    }
}
