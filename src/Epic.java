import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private final List<Integer> subtasksID;

    public Epic(String title, String description) {
        super(title, description);
        this.subtasksID = new ArrayList<>();
    }

    public List<Integer> getSubtasksID() {
        return subtasksID;
    }

    public void addSubtasksID(int subtaskID) {
        subtasksID.add(subtaskID);
    }

    public void clearSubtasksID() {
        subtasksID.clear();
    }

    @Override
    public String toString() {
        return "Epic{" + "id=" + id +
                ", title" + title +
                ", description='" + description +
                ", status=" + status +
                ", subtasksIds=" + subtasksID +
                "}";
    }
}
