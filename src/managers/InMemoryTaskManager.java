package managers;

import interfaces.HistoryManager;
import interfaces.TaskManager;
import models.Epic;
import models.SubTask;
import models.Task;
import models.TaskStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryTaskManager implements TaskManager {

    private final Map<Integer, Task> tasks = new HashMap<>();
    private final Map<Integer, Epic> epics = new HashMap<>();
    private final Map<Integer, SubTask> subTasks = new HashMap<>();

    private final HistoryManager historyManager = Managers.getDefaultHistory();

    private int idCounter = 1;

    private int generateId() {
        return idCounter++;
    }

    private final List<Task> history = new ArrayList<>();

    @Override
    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public List<SubTask> getAllSubTasks() {
        return new ArrayList<>(subTasks.values());
    }

    @Override
    public Task getTaskById(int id) {
        Task task = tasks.get(id);
        if (task != null) {
            historyManager.add(task);
        }
        return task;
    }

    @Override
    public Epic getEpicById(int id) {
        Epic epic = epics.get(id);
        if (epic != null) {
            historyManager.add(epic);
        }
        return epic;
    }

    @Override
    public SubTask getSubTaskById(int id) {
        SubTask subTask = subTasks.get(id);
        if (subTask != null) {
            historyManager.add(subTask);
        }
        return subTask;
    }

    @Override
    public Task createTask(Task task) {
        int newId = generateId();
        task.setId(newId);
        tasks.put(newId, task);
        return task;
    }

    @Override
    public Epic createEpic(Epic epic) {
        int newId = generateId();
        epic.setId(newId);
        epics.put(newId, epic);
        return epic;
    }

    @Override
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

    @Override
    public void updateTask(Task task) {
        if (tasks.containsKey(task.getId())) {
            tasks.put(task.getId(), task);
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        if (epic != null && epics.containsKey(epic.getId())) {
            Epic savedEpic = epics.get(epic.getId());
            savedEpic.setName(epic.getName());
            savedEpic.setDescription(epic.getDescription());
        }
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        if (subTask != null && subTasks.containsKey(subTask.getId())) {
            SubTask savedSubTask = subTasks.get(subTask.getId());

            // Если у подзадачи поменялся эпик — перепривязываем
            if (savedSubTask.getEpicId() != subTask.getEpicId()) {
                Epic oldEpic = epics.get(savedSubTask.getEpicId());
                if (oldEpic != null) {
                    oldEpic.removeSubtaskId(subTask.getId());
                    updateEpicStatus(oldEpic);
                }

                Epic newEpic = epics.get(subTask.getEpicId());
                if (newEpic != null) {
                    newEpic.addSubtaskId(subTask.getId());
                    updateEpicStatus(newEpic);
                }
            }

            subTasks.put(subTask.getId(), subTask);

            Epic epic = epics.get(subTask.getEpicId());
            if (epic != null) {
                updateEpicStatus(epic);
            }
        }
    }

    @Override
    public void deleteAllTasks() {
        for (Task task : tasks.values()){
            historyManager.remove(task.getId());
        }

        tasks.clear();
    }

    @Override
    public void deleteAllEpics() {
        for (Epic epic : epics.values()){
            historyManager.remove(epic.getId());
        }

        for (SubTask subTask: subTasks.values()){
            historyManager.remove(subTask.getId());
        }
        epics.clear();
        subTasks.clear();
    }

    @Override
    public void deleteAllSubTasks() {
        for (SubTask subTask: subTasks.values()){
            historyManager.remove(subTask.getId());
        }
        for (Epic epic : epics.values()) {
            epic.clearSubtaskIds();
            updateEpicStatus(epic);
        }
        subTasks.clear();
    }

    @Override
    public void deleteTaskById(int id) {
        tasks.remove(id);
        historyManager.remove(id);
    }

    @Override
    public void deleteEpicById(int id) {
        Epic epic = epics.remove(id);
        if (epic != null) {
            for (Integer subId : epic.getSubtaskIds()) {
                subTasks.remove(subId);
                historyManager.remove(subId);
            }
            historyManager.remove(id);
        }
    }

    @Override
    public void deleteSubTaskById(int id) {
        SubTask subTask = subTasks.remove(id);

        if (subTask != null) {
            Epic epic = epics.get(subTask.getEpicId());
            if (epic != null) {
                epic.removeSubtaskId(id);
                updateEpicStatus(epic);
            }

            historyManager.remove(id);
        }
    }

    @Override
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

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }
}