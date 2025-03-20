package http.handler;

import com.sun.net.httpserver.HttpExchange;
import manager.TaskManager;
import model.Epic;
import model.Subtask;

import java.io.IOException;
import java.util.List;

public class EpicsHandler extends BaseTaskHandler<Epic> {

    public EpicsHandler(TaskManager manager) {
        super(manager, Epic.class);
    }

    @Override
    protected String getBasePath() {
        return "/epics";
    }

    @Override
    protected void handleGet(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        if (path.contains("/subtasks")) {
            String idPath = path
                    .replaceFirst("/epics/", "")
                    .replace("/subtasks", "")
                    .trim();
            try {
                int epicId = Integer.parseInt(idPath);
                Epic epic = manager.getEpic(epicId);
                if (epic == null) {
                    sendNotFound(exchange);
                    return;
                }
                List<Subtask> subtasks = manager.getAllSubtasks().stream()
                        .filter(subtask -> subtask.getEpicId() == epicId)
                        .toList();
                sendText(exchange, gson.toJson(subtasks), 200);
            } catch (NumberFormatException e) {
                sendNotFound(exchange);
            }
        } else {
            List<Epic> epics = manager.getAllEpics();
            sendText(exchange, gson.toJson(epics), 200);
        }
    }

    @Override
    protected void handleDelete(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath().replaceFirst(getBasePath() + "/", "");
        try {
            if (path.isEmpty()) {
                manager.deleteAllSubtasks();
                manager.deleteAllEpics();
                sendText(exchange, "Все эпики удалены", 200);
            } else {
                int id = Integer.parseInt(path);
                Epic epic = manager.getEpic(id);
                if (epic == null) {
                    sendNotFound(exchange);
                    return;
                }
                manager.getAllSubtasks().stream()
                        .filter(subtask -> subtask.getEpicId() == id)
                        .forEach(subtask -> manager.deleteTaskById(subtask.getId()));
                manager.deleteTaskById(id);
                sendText(exchange, "Эпик с ID " + id + " удалён", 200);
            }
        } catch (NumberFormatException e) {
            sendNotFound(exchange);
        } catch (Exception e) {
            sendServerError(exchange, "Ошибка удаления эпиков: " + e.getMessage());
        }
    }

    @Override
    protected List<Epic> getAllTasks() {
        return manager.getAllEpics();
    }

    @Override
    protected Epic getTaskById(int id) {
        return manager.getEpic(id);
    }

    @Override
    protected void createTask(Epic epic) {
        manager.createEpic(epic);
    }

    @Override
    protected void deleteAllTasks() {
        manager.deleteAllEpics();
    }
}
