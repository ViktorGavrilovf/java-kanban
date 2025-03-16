package http.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manager.TaskManager;
import model.Task;

import java.io.IOException;
import java.util.List;

public class TasksHandler extends BaseHttpHandler implements HttpHandler {

    private TaskManager manager;

    public TasksHandler(TaskManager manager) {
        this.manager = manager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        switch (exchange.getRequestMethod()) {
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

    private void handleGet(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String[] parts = path.split("/");
        if (parts.length == 2) {
            List<Task> tasks = manager.getAllTasks();
            sendText(exchange, gson.toJson(tasks), 200);
        } else if (parts.length == 3) {
            try {
                Task task = manager.getTask(Integer.parseInt(parts[2]));
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

    private void handlePost(HttpExchange exchange) throws IOException {
        try {
            String json = readText(exchange);
            Task task = gson.fromJson(json, Task.class);
            if (task == null) {
                System.out.println("Ошибка: Gson не смог создать объект Task!");
                exchange.sendResponseHeaders(400, 0);
                exchange.close();
                return;
            }

            if (task.getId() > 0) {
                System.out.println("Обновление задачи с ID: " + task.getId());
                manager.updateTask(task);
                exchange.sendResponseHeaders(200, 0);
            } else {
                try {
                    manager.createTask(task);
                    exchange.sendResponseHeaders(201, 0);
                } catch (IllegalArgumentException e) {
                    System.out.println("Ошибка: задача пересекается с другой задачей");
                    sendHasInteractions(exchange);
                }
            }
        } catch (Exception e) {
            sendServerError(exchange, "Ошибка обработки запроса");
        } finally {
            exchange.close();
        }
    }

    private void handleDelete(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath().replaceFirst("/tasks/", "");
        try {
            if (path.isEmpty()) {
                manager.deleteAllTasks();
                sendText(exchange, "Все задачни удалены", 200);
            } else {
                int id = Integer.parseInt(path);
                Task task = manager.getTask(id);
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
}
