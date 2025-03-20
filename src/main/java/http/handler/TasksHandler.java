package http.handler;

import manager.TaskManager;
import model.Task;

import java.util.List;

public class TasksHandler extends BaseTaskHandler<Task> {

    public TasksHandler(TaskManager manager) {
        super(manager, Task.class);
    }

    @Override
    protected String getBasePath() {
        return "/tasks";
    }

    @Override
    protected List<Task> getAllTasks() {
        return manager.getAllTasks();
    }

    @Override
    protected Task getTaskById(int id) {
        return manager.getTask(id);
    }

    @Override
    protected void createTask(Task task) {
        manager.createTask(task);
    }

    @Override
    protected void deleteAllTasks() {
        manager.deleteAllTasks();
    }
}
