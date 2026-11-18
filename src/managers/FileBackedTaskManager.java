package managers;

import interfaces.HistoryManager;
import interfaces.TaskManager;
import models.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager implements TaskManager {

    private final File file;

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    public static String toString(Task task) {
        TaskType type = TaskType.TASK; // По умолчанию обычная задача
        String epicId = "";

        // если task это SubTask, то дай работать с ним через объект subTask
        if (task instanceof SubTask subTask) {
            type = TaskType.SUBTASK;
            epicId = String.valueOf(subTask.getEpicId());
        } else if (task instanceof Epic epic) {
            type = TaskType.EPIC;
        }

        return task.getId() + "," + type + "," + task.getName() + "," + task.getStatus()
                + "," + task.getDescription() + "," + epicId;
    }

    public static Task fromString(String value) {
        String[] fields = value.split(",", -1);

        int id = Integer.parseInt(fields[0]);
        TaskType type = TaskType.valueOf(fields[1]);
        String name = fields[2];
        TaskStatus status = TaskStatus.valueOf(fields[3]);
        String description = fields[4];

        return switch (type) {
            case TASK -> new Task(name, description, id, status);
            case EPIC -> new Epic(name, description, id, status);
            case SUBTASK -> {
                // yield показывает, что конкретно должен вернуть case
                int epicId = Integer.parseInt(fields[5]);
                yield new SubTask(name, description, id, status, epicId);
            }
        };

    }

    public static String toString(HistoryManager manager) {
        List<Task> history = manager.getHistory();
        if (history.isEmpty()) {
            return "";
        }

        String result = "";
        for (int i = 0; i < history.size(); i++) {
            result += history.get(i).getId();
            if (i < history.size() - 1) {
                result += ",";
            }
        }
        return result;
    }

    public static List<Integer> historyFromString(String value) {
        List<Integer> ids = new ArrayList<>();

        if (value == null || value.isEmpty()) {
            return ids;
        }

        String[] parts = value.split(",");
        for (String part : parts) {
            ids.add(Integer.parseInt(part));
        }
        return ids;
    }

    // TODO: Заглушка
    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager manager = new FileBackedTaskManager(file);

        // До пустой строки
        try {
            String content = Files.readString(file.toPath());
            String[] lines = content.split("\n");

            int i = 1;

            while (i < lines.length && !lines[i].isBlank()) {
                Task task = fromString(lines[i]);

                if (task instanceof Epic) {
                    manager.epics.put(task.getId(), (Epic) task);
                } else if (task instanceof SubTask subTask) {
                    manager.subTasks.put(subTask.getId(), subTask);

                    Epic epic = manager.epics.get(subTask.getEpicId());
                    if (epic != null) {
                        epic.addSubtaskId(subTask.getId());
                    }
                } else {
                    manager.tasks.put(task.getId(), task);
                }

                // Запоминаем самый большой id, который уже есть в файле и обновляем счётчик id
                if (task.getId() >= manager.idCounter) {
                    manager.idCounter = task.getId() + 1;
                }

                i++;
            }

            // Пропускаем пустую строку
            i++;

            // TODO Восстановление истории
            if (i < lines.length && !lines[i].isBlank()) {
                List<Integer> historyIds = historyFromString(lines[i]);

                for (Integer id : historyIds) {
                    // Ищем в обычных задачах
                    Task task = manager.tasks.get(id);
                    // Ищем в эпиках
                    if (task == null) {
                        task = manager.epics.get(id);
                    }
                    // Ищем в сабтасках
                    if (task == null) {
                        task = manager.subTasks.get(id);
                    }
                    // Нашли - добавляем в историю
                    if (task != null) {
                        manager.historyManager.add(task);
                    }
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при загрузке из файла", e);
        }

        return manager;
    }

    private void save() {
        try (FileWriter writer = new FileWriter(file)) {
            writer.write("id,type,name,status,description,epic\n");

            for (Task task : getAllTasks()) {
                writer.write(toString(task) + "\n");
            }

            for (Epic epic : getAllEpics()) {
                writer.write(toString(epic) + "\n");
            }

            for (SubTask subTask : getAllSubTasks()) {
                writer.write(toString(subTask) + "\n");
            }

            writer.write("\n");

            //protected - дает доступ к полю (наследника)historyManager
            writer.write(toString(historyManager));
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при сохранении в файл", e);
        }
    }

    @Override
    public Task getTaskById(int id) {
        Task task = super.getTaskById(id);
        save();
        return task;
    }

    @Override
    public Epic getEpicById(int id) {
        Epic epic = super.getEpicById(id);
        save();
        return epic;
    }

    @Override
    public SubTask getSubTaskById(int id) {
        SubTask subTask = super.getSubTaskById(id);
        save();
        return subTask;
    }

    @Override
    public Task createTask(Task task) {
        Task created = super.createTask(task);
        save();
        return created;
    }

    @Override
    public Epic createEpic(Epic epic) {
        Epic created = super.createEpic(epic);
        save();
        return created;
    }

    @Override
    public SubTask createSubTask(SubTask subTask) {
        SubTask created = super.createSubTask(subTask);
        save();
        return created;
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        super.updateSubTask(subTask);
        save();
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    @Override
    public void deleteAllEpics() {
        super.deleteAllEpics();
        save();
    }

    @Override
    public void deleteAllSubTasks() {
        super.deleteAllSubTasks();
        save();
    }

    @Override
    public void deleteTaskById(int id) {
        super.deleteTaskById(id);
        save();
    }

    @Override
    public void deleteEpicById(int id) {
        super.deleteEpicById(id);
        save();
    }

    @Override
    public void deleteSubTaskById(int id) {
        super.deleteSubTaskById(id);
        save();
    }
}
