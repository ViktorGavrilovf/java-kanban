package manager;

import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryTaskManager implements TaskManager {
    protected final Map<Integer, Task> tasks = new HashMap<>();
    protected final Map<Integer, Epic> epics = new HashMap<>();
    protected final Map<Integer, Subtask> subtasks = new HashMap<>();
    private final HistoryManager historyManager = Managers.getDefaultHistory(); // Список для хранения истории просмотров
    private int idCounter = 0;

    private int generateId() {
        return ++idCounter;
    }

    @Override
    public Task createTask(String title, String description) {
        int id = generateId();
        Task task = new Task(id, title, description);
        tasks.put(id, task);
        return task;
    }

    @Override
    public Epic createEpic(String title, String description) {
        int id = generateId();
        Epic epic = new Epic(id, title, description);
        epics.put(id, epic);
        return epic;
    }

    @Override
    public Subtask createSubtask(String title, String description, int epicId) {
        int id = generateId();
        Subtask subtask = new Subtask(id, title, description, epicId);
        subtasks.put(id, subtask);

        Epic epic = epics.get(epicId);
        if (epic != null) {
            epic.addSubtasksId(subtask.getId());
            updateEpicStatus(epic);
        }
        return subtask;
    }

    @Override
    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public List<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public Task getTask(int id) {
        Task task = tasks.get(id);
        historyManager.add(task);
        return task;
    }

    @Override
    public Subtask getSubtask(int id) {
        Subtask subtask = subtasks.get(id);
        historyManager.add(subtask);
        return subtask;
    }

    @Override
    public Epic getEpic(int id) {
        Epic epic = epics.get(id);
        historyManager.add(epic);
        return epic;
    }


    @Override
    public void deleteTaskById(Integer id) {
        if (tasks.containsKey(id)) {
            tasks.remove(id);
        } else if (subtasks.containsKey(id)) {
            Subtask subtask = subtasks.remove(id);
            Epic epic = epics.get(subtask.getEpicId());
            epic.getSubtasksId().remove(id);
        } else if (epics.containsKey(id)) {
            Epic epic = epics.remove(id);
            for (Integer subId : new ArrayList<>(epic.getSubtasksId())) {
                subtasks.remove(subId);
            }
        }
    }

    @Override
    public void deleteAllTasks() {
        tasks.clear();
    }

    @Override
    public void deleteAllEpics() {
        deleteAllSubtasks();
        epics.clear();
    }

    @Override
    public void deleteAllSubtasks() {
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.clearSubtasksId();
            updateEpicStatus(epic);
        }
    }

    private void updateEpicStatus(Epic epic) {
        boolean allDone = true;
        boolean allNew = true;

        for (int subtaskId : epic.getSubtasksId()) {
            Subtask subtask = subtasks.get(subtaskId);
            if (subtask.getStatus() != TaskStatus.DONE) {
                allDone = false;
            }
            if (subtask.getStatus() != TaskStatus.NEW) {
                allNew = false;
            }
            if (!allDone && !allNew) {
                epic.setStatus(TaskStatus.IN_PROGRESS);
                return;
            }
        }

        if (allDone) {
            epic.setStatus(TaskStatus.DONE);
        } else  {
            epic.setStatus(TaskStatus.NEW);
        }
    }

    @Override
    public void updateTask(Subtask subtask) {
        if (subtasks.containsKey(subtask.getId())) {
            subtasks.put(subtask.getId(), subtask);
            Epic epic = epics.get(subtask.getEpicId());
            if (epic != null) {
                updateEpicStatus(epic);
            }
        }
    }


    @Override
    public List<Task> getHistory() { // Возвращаем историю просмотра
        return historyManager.getHistory();
    }
}
