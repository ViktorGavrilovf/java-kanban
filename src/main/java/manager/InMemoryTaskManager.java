package manager;

import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;

import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    protected TreeSet<Task> prioritizedTasks = new TreeSet<>(new TaskComparator());
    static class TaskComparator implements Comparator<Task> {
        @Override
        public int compare(Task task1, Task task2) {
            if ((task1.getStartTime() == null && task2.getStartTime() == null) ||
                    task1.getStartTime().equals(task2.getStartTime())) {
                return Integer.compare(task1.getId(), task2.getId());
            }
            if (task1.getStartTime() != null && task2.getStartTime() == null) {
                return -1;
            }
            if (task1.getStartTime() == null && task2.getStartTime() != null) {
                return 1;
            }
            return task1.getStartTime().compareTo(task2.getStartTime());
        }
    }
    protected final Map<Integer, Task> tasks = new HashMap<>();
    protected final Map<Integer, Epic> epics = new HashMap<>();
    protected final Map<Integer, Subtask> subtasks = new HashMap<>();

    private final HistoryManager historyManager = Managers.getDefaultHistory();
    private int idCounter = 0;

    private int generateId() {
        return ++idCounter;
    }

    @Override
    public Task createTask(Task task) {
        int id = generateId();
        task.setId(id);
        tasks.put(id, task);
        prioritizedTasks.add(task);
        return task;
    }

    @Override
    public Epic createEpic(Epic epic) {
        int id = generateId();
        epic.setId(id);
        epics.put(id, epic);
        return epic;
    }

    @Override
    public Subtask createSubtask(Subtask subtask) {
        int id = generateId();
        subtask.setId(id);
        subtasks.put(id, subtask);
        prioritizedTasks.add(subtask);

        Epic epic = epics.get(subtask.getEpicId());
        if (epic != null) {
            epic.addSubtasksId(subtask.getId());
            updateEpicStatus(epic);
            updateTimeEpic(epic);
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
            Task task = tasks.get(id);
            prioritizedTasks.remove(task);
            tasks.remove(id);
            historyManager.remove(id);
        } else if (subtasks.containsKey(id)) {
            Subtask subtask = subtasks.remove(id);
            historyManager.remove(id);
            prioritizedTasks.remove(subtask);
            Epic epic = epics.get(subtask.getEpicId());
            epic.getSubtasksId().remove(id);
            updateTimeEpic(epic);
            updateEpicStatus(epic);
        } else if (epics.containsKey(id)) {
            Epic epic = epics.remove(id);
            epic.getSubtasksId().forEach(subId -> {
                prioritizedTasks.remove(subtasks.get(subId));
                subtasks.remove(subId);
                historyManager.remove(subId);});
        }
    }

    @Override
    public void deleteAllTasks() {
        tasks.clear();
        prioritizedTasks.clear();
    }

    @Override
    public void deleteAllEpics() {
        deleteAllSubtasks();
        epics.clear();
        prioritizedTasks.clear();
    }

    @Override
    public void deleteAllSubtasks() {
        subtasks.clear();
        prioritizedTasks.clear();
        for (Epic epic : epics.values()) {
            epic.clearSubtasksId();
            updateEpicStatus(epic);
        }
    }

    private void updateEpicStatus(Epic epic) {
        List<Subtask> subtaskList = epic.getSubtasksId().stream()
                .map(subtasks::get)
                .filter(Objects::nonNull)
                .toList();

        if (subtaskList.isEmpty()) {
            epic.setStatus(TaskStatus.NEW);
            return;
        }

        boolean allNew = subtaskList.stream().allMatch(subtask -> subtask.getStatus().equals(TaskStatus.NEW));
        boolean allDone = subtaskList.stream().allMatch(subtask -> subtask.getStatus().equals(TaskStatus.DONE));

        if (allNew) {
            epic.setStatus(TaskStatus.NEW);
        } else if (allDone) {
            epic.setStatus(TaskStatus.DONE);
        } else {
            epic.setStatus(TaskStatus.IN_PROGRESS);
        }
    }

    @Override
    public void updateTask(Subtask subtask) {
        if (subtasks.containsKey(subtask.getId())) {
            subtasks.put(subtask.getId(), subtask);
            prioritizedTasks.remove(subtask);
            prioritizedTasks.add(subtask);
            Epic epic = epics.get(subtask.getEpicId());
            if (epic != null) {
                updateEpicStatus(epic);
            }
        }
    }

    private void updateTimeEpic(Epic epic) {
        List<Subtask> subtaskList = epic.getSubtasksId().stream()
                .map(subtasks::get)
                .filter(Objects::nonNull)
                .filter(subtask -> subtask.getStartTime() != null && subtask.getDuration() != null)
                .toList();
        epic.updateEpicTime(subtaskList);
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }
}
