package model;

import manager.Managers;
import manager.TaskManager;

public class Subtask extends Task {
    private final int epicId;

    public Subtask(int id, String title, String description, int epicId) {
        super(id, title, description);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    @Override
    public String toString() {
        return "model.Subtask{" +
                "id=" + id +
                ", title='" + title +
                ", description='" + description +
                ", status=" + status +
                ", epicId=" + epicId +
                '}';
    }
}
