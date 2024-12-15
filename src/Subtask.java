public class Subtask extends Task {
    private final int epicID;

    public Subtask(String title, String description, int epicID) {
        super(title, description);
        this.epicID = epicID;
    }

    public int getEpicId() {
        return epicID;
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "id=" + id +
                ", title='" + title +
                ", description='" + description +
                ", status=" + status +
                ", epicId=" + epicID +
                '}';
    }
}
