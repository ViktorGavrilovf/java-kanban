package model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Epic extends Task {
    private List<Integer> subtasksId;
    protected LocalDateTime endTime;

    public Epic(int id, String title, String description) {
        super(id, title, description);
        this.subtasksId = new ArrayList<>();
        this.startTime = null;
        this.duration = Duration.ZERO;
        this.endTime = null;
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
        if (subtasksId == null) {
            subtasksId = new ArrayList<>();
        }
        subtasksId.clear();
    }

    @Override
    public TaskType getType() {
        return TaskType.EPIC;
    }

    public void updateEpicTime(List<Subtask> subtasks) {
        if (subtasks.isEmpty()) {
            this.startTime = null;
            this.duration = Duration.ZERO;
            this.endTime = null;
            return;
        }

        this.startTime = subtasks.stream()
                .map(Subtask::getStartTime)
                .filter(Objects::nonNull)
                .min(LocalDateTime::compareTo)
                .orElse(null);

        this.endTime = subtasks.stream()
                .map(Subtask::getEndTime)
                .filter(Objects::nonNull)
                .max(LocalDateTime::compareTo)
                .orElse(null);

        this.duration = subtasks.stream()
                .map(Subtask::getDuration)
                .filter(Objects::nonNull)  // добавляем фильтр на null-ы
                .reduce(Duration.ZERO, Duration::plus);
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    @Override
    public String toString() {
        return "model.Epic{" + "id=" + id +
                ", title=" + title +
                ", description='" + description +
                ", status=" + status +
                ", startTime=" + (startTime != null ? startTime.toString() : "не задано") +
                ", duration=" + (duration != null ? duration.toMinutes() + " минуты" : "не задано") +
                ", subtasksIds=" + subtasksId +
                "}";
    }
}
