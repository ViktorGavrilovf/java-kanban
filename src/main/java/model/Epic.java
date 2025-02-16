package model;

import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private final List<Integer> subtasksId;

    public Epic(int id, String title, String description) {
        super(id, title, description);
        this.subtasksId = new ArrayList<>();
    }

    public List<Integer> getSubtasksId() {
        return subtasksId;
    }

    public void addSubtasksId(int subtaskId) {
        if (subtaskId != this.getId()) {
            subtasksId.add(subtaskId);
        }
    }

    public void clearSubtasksId() {
        subtasksId.clear();
    }

    @Override
    public TaskType getType() {
        return TaskType.EPIC;
    }

    @Override
    public String toString() {
        return "model.Epic{" + "id=" + id +
                ", title" + title +
                ", description='" + description +
                ", status=" + status +
                ", subtasksIds=" + subtasksId +
                "}";
    }
}
