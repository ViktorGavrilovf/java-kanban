package http.handler;

import exception.EpicNotFoundException;
import manager.TaskManager;
import model.Subtask;

import java.util.List;

public class SubtaskHandler extends BaseTaskHandler<Subtask> {

    public SubtaskHandler(TaskManager manager) {
        super(manager, Subtask.class);
    }

    @Override
    protected String getBasePath() {
        return "/subtasks";
    }

    @Override
    protected List<Subtask> getAllTasks() {
        return manager.getAllSubtasks();
    }

    @Override
    protected Subtask getTaskById(int id) {
        return manager.getSubtask(id);
    }

    @Override
    protected void createTask(Subtask subtask) {
        if (subtask.getEpicId() <= 0 || manager.getEpic(subtask.getEpicId()) == null) {
            throw new EpicNotFoundException("Эпик не найден");
        }
        manager.createSubtask(subtask);
    }

    @Override
    protected void deleteAllTasks() {
        manager.deleteAllSubtasks();
    }
}
