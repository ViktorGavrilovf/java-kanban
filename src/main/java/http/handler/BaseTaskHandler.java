package http.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import exception.EpicNotFoundException;
import manager.TaskManager;
import model.Epic;
import model.Task;

import java.io.IOException;
import java.util.List;

public abstract class BaseTaskHandler<T extends Task> extends BaseHttpHandler implements HttpHandler {

    protected final TaskManager manager;
    private final Class<T> clazz;

    public BaseTaskHandler(TaskManager manager, Class<T> clazz) {
        this.manager = manager;
        this.clazz = clazz;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        switch (method) {
            case "GET":
                handleGet(exchange);
                break;
            case "POST":
                handlePost(exchange);
                break;
            case "DELETE":
                handleDelete(exchange);
                break;
            default:
                exchange.sendResponseHeaders(405, 0);
                exchange.close();
        }
    }

    protected void handleGet(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath().replaceFirst(getBasePath() + "/", "");
        if (path.isEmpty()) {
            List<T> tasks = getAllTasks();
            sendText(exchange, gson.toJson(tasks), 200);
        } else {
            try {
                int id = Integer.parseInt(path);
                T task = getTaskById(id);
                if (task != null) {
                    sendText(exchange, gson.toJson(task), 200);
                } else {
                    sendNotFound(exchange);
                }
            } catch (NumberFormatException e) {
                sendNotFound(exchange);
            }
        }
    }

    protected void handlePost(HttpExchange exchange) throws IOException {
        try {
            String json = readText(exchange);
            T task = gson.fromJson(json, clazz);
            if (task == null) {
                exchange.sendResponseHeaders(400, 0);
                exchange.close();
                return;
            }

            if (task instanceof Epic epic) {
                if (epic.getSubtasksId() == null) {
                    epic.clearSubtasksId();
                }
            }

            if (task.getId() > 0) {
                manager.updateTask(task);
                exchange.sendResponseHeaders(200, 0);
            } else {
                try {
                    createTask(task);
                    exchange.sendResponseHeaders(201, 0);
                } catch (EpicNotFoundException e) {
                    sendNotFound(exchange);
                } catch (IllegalArgumentException e) {
                    sendHasInteractions(exchange);
                }
            }
        } catch (Exception e) {
            sendServerError(exchange, "Ошибка обработки запроса" + e.getMessage());
        } finally {
            exchange.close();
        }
    }

    protected void handleDelete(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath().replaceFirst(getBasePath() + "/", "");
        try {
            if (path.isEmpty()) {
                deleteAllTasks();
                sendText(exchange, "Все задачи удалны", 200);
            } else {
                int id = Integer.parseInt(path);
                T task = getTaskById(id);
                if (task == null) {
                    sendNotFound(exchange);
                    return;
                }
                manager.deleteTaskById(id);
                sendText(exchange, "Задача с ID " + id + " удалена", 200);
            }
        } catch (NumberFormatException e) {
            sendNotFound(exchange);
        } catch (Exception e) {

            sendServerError(exchange, "Ошибка обработки запроса");
        } finally {
            exchange.close();
        }
    }

    protected abstract String getBasePath();
    protected abstract List<T> getAllTasks();
    protected abstract T getTaskById(int id);
    protected abstract void createTask(T task);
    protected abstract void deleteAllTasks();
}
