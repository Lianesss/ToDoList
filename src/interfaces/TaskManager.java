package interfaces;

import models.Epic;
import models.SubTask;
import models.Task;

import java.util.List;

public interface TaskManager {

    // Получение всех задач
    List<Task> getAllTasks();
    List<Epic> getAllEpics();
    List<SubTask> getAllSubTasks();

    // Получение по id
    Task getTaskById(int id);
    Epic getEpicById(int id);
    SubTask getSubTaskById(int id);

    // Создание
    Task createTask(Task task);
    Epic createEpic(Epic epic);
    SubTask createSubTask(SubTask subTask);

    // Обновление
    void updateTask(Task task);
    void updateEpic(Epic epic);
    void updateSubTask(SubTask subTask);

    // Удаление всех
    void deleteAllTasks();
    void deleteAllEpics();
    void deleteAllSubTasks();

    // Удаление по id
    void deleteTaskById(int id);
    void deleteEpicById(int id);
    void deleteSubTaskById(int id);

    // Подзадачи конкретного эпика
    List<SubTask> getSubTaskOfEpic(int epicId);
    List<Task> getHistory();
}