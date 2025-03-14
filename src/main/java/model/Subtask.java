package model;

import java.time.Duration;
import java.time.LocalDateTime;

public class Subtask extends Task {
    private final int epicId;

    public Subtask(int id, String title, String description, int epicId) {
        super(id, title, description);
        this.epicId = epicId;
    }

    public Subtask(int id, String title, String description, LocalDateTime startTime, Duration duration, int epicId) {
        super(id, title, description, startTime, duration);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    @Override
    public TaskType getType() {
        return TaskType.SUBTASK;
    }

    @Override
    public String toString() {
        return "model.Subtask{" +
                "id=" + id +
                ", title='" + title +
                ", description='" + description +
                ", status=" + status +
                ", startTime=" + (startTime != null ? startTime.toString() : "не задано") +
                ", duration=" + (duration != null ? duration.toMinutes() + " минуты" : "не задано") +
                ", epicId=" + epicId +
                '}';
    }
}
