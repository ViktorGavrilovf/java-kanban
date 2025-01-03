package model;

import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private final List<Integer> subtasksId;

    public Epic(String title, String description) {
        super(title, description);
        this.subtasksId = new ArrayList<>();
    }

    public List<Integer> getSubtasksId() {
        return subtasksId;
    }

    public void addSubtasksId(int subtaskId) {
        if (subtaskId != this.getId()) {        // Проверяем, что ID подзадачи не совпадает с ID эпика
            subtasksId.add(subtaskId);
        }
    }

    public void clearSubtasksId() {
        subtasksId.clear();
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
