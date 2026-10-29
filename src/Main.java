import model.*;

public class Main {
    public static void main(String[] args) {
        InMemoryTaskManager manager = new InMemoryTaskManager();

        Task task1 = manager.createTask(new Task("Учеба", "Сделать лабу по Java"));
        Task task2 = manager.createTask(new Task("Магазин", "Купить продукты"));

        Epic epic1 = manager.createEpic(new Epic("ЛАБА_3", "Доделать лабу"));
        SubTask subTask1 = manager.createSubTask(new SubTask("Дописать методы в inMemoryTaskManager", "Методы удаления всех подзадач и конкретного эпика", epic1.getId()));
        SubTask subTask2 = manager.createSubTask(new SubTask("Исправить ошибки", "дописать конструктор SubTask", epic1.getId()));

        Epic epic2 = manager.createEpic(new Epic("Игра", "Исправление багов"));
        SubTask subTask3 = manager.createSubTask(new SubTask("Исправить ошибки", "не регистрирует попадание", epic2.getId()));

        System.out.println("\n=== ВСЕ ЗАДАЧИ ===");
        System.out.println("\nЗадачи:");
        for (Task task : manager.getAllTasks()) {
            System.out.println("  - " + task);
        }

        System.out.println("\nЭпики:");
        for (Epic epic : manager.getAllEpics()) {
            System.out.println("  - " + epic);
        }

        System.out.println("\nПодзадачи:");
        for (SubTask subTask : manager.getAllSubTasks()) {
            System.out.println("  - " + subTask);
        }

        System.out.println("\n=== ИЗМЕНЕНИЕ СТАТУСОВ ===");
        subTask1.setStatus(TaskStatus.DONE);
        manager.updateSubTask(subTask1);

        subTask2.setStatus(TaskStatus.DONE);
        manager.updateSubTask(subTask2);

        System.out.println("Эпик 1 после выполнения всех подзадач:\n  -> " + manager.getEpicById(epic1.getId()));

        System.out.println("\n=== УДАЛЕНИЕ (удаляем task1 и epic2 вместе с его подзадачей subTask3) ===");
        manager.deleteTaskById(task1.getId());
        manager.deleteEpicById(epic2.getId()); // Удалит epic2 и автоматически зачистит subTask3

        System.out.println("\nОставшиеся задачи:");
        for (Task task : manager.getAllTasks()) {
            System.out.println("  - " + task);
        }

        System.out.println("\nОставшиеся эпики:");
        for (Epic epic : manager.getAllEpics()) {
            System.out.println("  - " + epic);
        }

        System.out.println("\nОставшиеся подзадачи:");
        for (SubTask subTask : manager.getAllSubTasks()) {
            System.out.println("  - " + subTask);
        }
    }
}