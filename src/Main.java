import model.*;

public class Main {
    public static void main(String[] args) {
        InMemoryTaskManager manager = new InMemoryTaskManager();

        Task task1 = manager.createTask(new Task("Учеба", "Сделать лабу по Java"));
        Task task2 = manager.createTask(new Task("Магазин", "Купить продукты"));

        Epic epic1 = manager.createEpic(new Epic("ЛАБА_3", "Доделать лабу"));
        SubTask subTask1=manager.createSubTask(new SubTask("Дописать методы в inMemoryTaskManager", "Методы удаления всех подзадач и конкретного эпика", epic1.getId()));
        SubTask subTask2=manager.createSubTask(new SubTask("Исправить ошибки", "дописать конструктор SubTask", epic1.getId()));

        Epic epic2 = manager.createEpic(new Epic("Игра", "Исправление багов"));
        SubTask subTask3=manager.createSubTask(new SubTask("Исправить ошибки", "не регистрирует попадание", epic2.getId()));

        System.out.println("=== ЗАДАЧИ ===");
        System.out.println("task: " + manager.getAllTasks());
        System.out.println("epic: " + manager.getAllEpics());
        System.out.println("subTask " + manager.getAllSubTasks());

        System.out.println("\n=== ИЗМЕНЕНИЕ СТАТУСОВ ===");
        subTask1.setStatus(TaskStatus.DONE);
        manager.updateSubTask(subTask1);

        subTask2.setStatus(TaskStatus.DONE);
        manager.updateSubTask(subTask2);

        System.out.println("Эпик 1 после выполнения всех подзадач (должен быть DONE): " + manager.getEpicById(epic1.getId()));

        System.out.println("\n=== УДАЛЕНИЕ ===");
        manager.deleteTaskById(task1.getId());
        manager.deleteEpicById(epic2.getId());

        System.out.println("tasks после удаления: " + manager.getAllTasks());
        System.out.println("epics после удаления: " + manager.getAllEpics());
        System.out.println("subTasks после удаления: " + manager.getAllSubTasks());

    }

}