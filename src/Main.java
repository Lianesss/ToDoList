import interfaces.TaskManager;
import managers.Managers;
import models.*;

public class Main {
    public static void main(String[] args) {
        TaskManager manager = Managers.getDefault();

        // СОЗДАНИЕ
        System.out.println("СОЗДАНИЕ");

        Task task1 = manager.createTask(new Task("Задача 1", "Описание 1"));
        Task task2 = manager.createTask(new Task("Задача 2", "Описание 2"));

        Epic epic = manager.createEpic(new Epic("Эпик", "Описание эпика"));
        SubTask sub1 = manager.createSubTask(new SubTask("Подзадача 1", "Описание", epic.getId()));
        SubTask sub2 = manager.createSubTask(new SubTask("Подзадача 2", "Описание", epic.getId()));

        System.out.println("Задачи:");
        System.out.println("  " + task1);
        System.out.println("  " + task2);

        System.out.println("\nЭпик:");
        System.out.println("  " + epic);

        System.out.println("\nПодзадачи:");
        System.out.println("  " + sub1);
        System.out.println("  " + sub2);

        System.out.println("\nИстория после создания: " + manager.getHistory());

        // ПОЛУЧЕНИЕ ВСЕХ
        System.out.println("\nПОЛУЧЕНИЕ ВСЕХ");
        System.out.println("Все задачи:    " + manager.getAllTasks());
        System.out.println("Все эпики:     " + manager.getAllEpics());
        System.out.println("Все подзадачи: " + manager.getAllSubTasks());

        // ПОЛУЧЕНИЕ ПО ID
        System.out.println("\nПОЛУЧЕНИЕ ПО ID");

        manager.getTaskById(task1.getId());
        System.out.println("\nИстория после getTaskById(task1):");
        System.out.println("  " + manager.getHistory());

        manager.getEpicById(epic.getId());
        System.out.println("\nИстория после getEpicById(epic):");
        System.out.println("  " + manager.getHistory());

        manager.getSubTaskById(sub1.getId());
        System.out.println("\nИстория после getSubTaskById(sub1):");
        System.out.println("  " + manager.getHistory());

        manager.getTaskById(task2.getId());
        System.out.println("\nИстория после getTaskById(task2):");
        System.out.println("  " + manager.getHistory());

        // ОБНОВЛЕНИЕ
        System.out.println("\nОБНОВЛЕНИЕ");
        task1.setStatus(TaskStatus.IN_PROGRESS);
        manager.updateTask(task1);
        System.out.println("task1 после обновления: " + task1);
        System.out.println("История после updateTask (не изменилась):");
        System.out.println("  " + manager.getHistory());

        // ПОДЗАДАЧИ ЭПИКА
        System.out.println("\nПОДЗАДАЧИ ЭПИКА");
        System.out.println("  " + manager.getSubTaskOfEpic(epic.getId()));
    }
}