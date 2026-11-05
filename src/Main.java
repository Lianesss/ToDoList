import interfaces.TaskManager;
import managers.Managers;
import models.*;

public class Main {
    public static void main(String[] args) {
        TaskManager manager = Managers.getDefault();
        System.out.println("СОЗДАНИЕ");

        Task task1 = manager.createTask(new Task("Задача 1", "Описание 1"));
        Task task2 = manager.createTask(new Task("Задача 2", "Описание 2"));

        // Эпик с тремя подзадачами
        Epic epicWithSubs = manager.createEpic(new Epic("Эпик с подзадачами", "Описание"));
        SubTask sub1 = manager.createSubTask(new SubTask("Подзадача 1", "Описание", epicWithSubs.getId()));
        SubTask sub2 = manager.createSubTask(new SubTask("Подзадача 2", "Описание", epicWithSubs.getId()));
        SubTask sub3 = manager.createSubTask(new SubTask("Подзадача 3", "Описание", epicWithSubs.getId()));

        // Эпик без подзадач
        Epic emptyEpic = manager.createEpic(new Epic("Пустой эпик", "Без подзадач"));

        System.out.println("Создано:");
        System.out.println("  " + task1);
        System.out.println("  " + task2);
        System.out.println("  " + epicWithSubs);
        System.out.println("  " + sub1);
        System.out.println("  " + sub2);
        System.out.println("  " + sub3);
        System.out.println("  " + emptyEpic);

        // Запрашиваем задачи в разном порядке
        System.out.println("\nПРОСМОТРЫ");

        manager.getTaskById(task1.getId());
        System.out.println("После task1: " + manager.getHistory());

        manager.getEpicById(epicWithSubs.getId());
        System.out.println("После epicWithSubs: " + manager.getHistory());

        manager.getSubTaskById(sub2.getId());
        System.out.println("После sub2: " + manager.getHistory());

        manager.getTaskById(task2.getId());
        System.out.println("После task2: " + manager.getHistory());

        manager.getSubTaskById(sub1.getId());
        System.out.println("После sub1: " + manager.getHistory());

        // Повторный просмотр task1 (должен переместиться в конец, без дубля)
        manager.getTaskById(task1.getId());
        System.out.println("После повторного task1 (без дублей): " + manager.getHistory());

        manager.getEpicById(emptyEpic.getId());
        System.out.println("После emptyEpic: " + manager.getHistory());

        manager.getSubTaskById(sub3.getId());
        System.out.println("После sub3: " + manager.getHistory());

        // Удаление задачи, которая есть в истории
        System.out.println("\nУДАЛЕНИЕ task2");
        manager.deleteTaskById(task2.getId());
        System.out.println("История после удаления task2: " + manager.getHistory());

        //Удаление эпика с тремя подзадачами
        System.out.println("\nУДАЛЕНИЕ эпика с подзадачами");
        manager.deleteEpicById(epicWithSubs.getId());
        System.out.println("История после удаления эпика и его подзадач:");
        System.out.println("  " + manager.getHistory());
    }
}