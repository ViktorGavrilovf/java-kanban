package manager;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class ManagersTest {

    @Test
    void testGetDefaultManagers() {
        TaskManager taskManager = Managers.getDefault();
        assertNotNull(taskManager, "taskManager должен быть инициализирован.");

        HistoryManager historyManager = Managers.getDefaultHistory();
        assertNotNull(historyManager, "historyManager должен быть инициализирован.");
    }
}