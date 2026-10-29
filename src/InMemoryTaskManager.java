import model.Epic;
import model.SubTask;
import model.Task;
import model.TaskStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryTaskManager {
    //           контракт словаря                  реализация
    private final Map<Integer, Task> tasks = new HashMap<>();
    private final Map<Integer, Epic> epics = new HashMap<>();
    private final Map<Integer, SubTask> subTasks = new HashMap<>();

    private int idCounter = 1;

    private int generateId() {
        return idCounter++;
    }

    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values()); // короче копия, чтобы менять можно было только через класс инМемори
        // если просто tasks.values() то тот кто вызвал метод сможет менять оригинал
    }

    public List<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    public List<SubTask> getAllSubTasks() {
        return new ArrayList<>(subTasks.values());
    }

    public Task getTaskById(int id) {
        return tasks.get(id);
    }

    public Epic getEpicById(int id) {
        return epics.get(id);
    }

    public SubTask getSubTaskById(int id) {
        return subTasks.get(id);
    }

    public Task createTask(Task task) {
        int newId = generateId();
        task.setId(newId);

        tasks.put(newId, task);
        return task;
    }

    public Epic createEpic(Epic epic) {
        int newId = generateId();
        epic.setId(newId);

        epics.put(newId, epic);
        return epic;
    }

    public SubTask createSubTask(SubTask subTask) {
        Epic epic = epics.get(subTask.getEpicId());
        if (epic == null) {
            return null;
        }

        int newId = generateId();
        subTask.setId(newId);

        subTasks.put(newId, subTask);
        epic.addSubtaskId(newId);

        updateEpicStatus(epic);
        return subTask;
    }

    private void updateEpicStatus(Epic epic) {
        List<Integer> subTasksIds = epic.getSubtaskIds();

        boolean allNew = true;
        boolean allDone = true;

        //если подзадач нет - статус NEW
        if (subTasksIds.isEmpty()) {
            epic.setStatus(TaskStatus.NEW);
            return;
        }

        for (Integer id : subTasksIds) {
            SubTask subTask = subTasks.get(id);
            if (subTask == null) continue;

            TaskStatus status = subTask.getStatus();

            if (status != TaskStatus.NEW) {
                allNew = false;
            }

            if (status != TaskStatus.DONE) {
                allDone = false;
            }
        }

        if (allNew) {
            epic.setStatus(TaskStatus.NEW);
        } else if (allDone) {
            epic.setStatus(TaskStatus.DONE);
        } else {
            epic.setStatus(TaskStatus.IN_PROGRESS);
        }
    }

    public void updateTask(Task task) {

        // containsKey - есть ли ключ
        if (tasks.containsKey(task.getId())) {
            tasks.put(task.getId(), task);
        }
    }

    public void updateEpic(Epic epic) {
        if (epic != null && epics.containsKey(epic.getId())) {
            Epic savedEpic = epics.get(epic.getId());

            savedEpic.setName(epic.getName());
            savedEpic.setDescription(epic.getDescription());
        }
    }

    public void updateSubTask(SubTask subTask) {
        if (subTasks.containsKey(subTask.getId())) {
            subTasks.put(subTask.getId(), subTask);

            Epic epic = epics.get(subTask.getEpicId());
            if (epic != null) {
                updateEpicStatus(epic);
            }
        }
    }

    public void deleteAllTasks() {
        tasks.clear();
    }

    public void deleteAllEpics() {
        epics.clear();
        subTasks.clear();
    }

    public void deleteAllSubTasks() {
        // удаление всех списков подзадач и сброс на new
        for (Epic epic : epics.values()) {
            epic.clearSubtaskIds();
            updateEpicStatus(epic);
        }

        subTasks.clear();
    }

    //передаем ключ id, чтобы удалить значение
    public void deleteTaskById(int id) {
        tasks.remove(id);
    }

    public void deleteEpicById(int id) {
        Epic epic = epics.remove(id);
        if (epic != null) {
            for (Integer subId : epic.getSubtaskIds()) {
                subTasks.remove(subId);
            }
        }
    }

    public void deleteSubTaskById(int id) {
        SubTask subTask = subTasks.remove(id);

        if (subTask != null) {
            Epic epic = epics.get(subTask.getEpicId());
            if (epic != null) {
                epic.removeSubtaskId(id);
                updateEpicStatus(epic);
            }
        }
    }

    //подзадачи конкретного epic
    public List<SubTask> getSubTaskOfEpic(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic == null) {
            return new ArrayList<>();
        }

        List<SubTask> result = new ArrayList<>();
        for (Integer id : epic.getSubtaskIds()) {
            SubTask sub = subTasks.get(id);
            if (sub != null) {
                result.add(sub);
            }
        }
        return result;
    }
}
