package managers;

import interfaces.HistoryManager;
import interfaces.TaskManager;

public class Managers {
    //Static - Метод принадлежит классу, а не объекту. Можно вызывать без new
    public static TaskManager getDefault(){
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory(){
        return new InMemoryHistoryManager();
    }



}
//Утилитарный класс (или класс-утилита) в Java —
// это вспомогательный класс,
// который содержит только статические методы
// и статические переменные (константы) и создается для выполнения
// часто повторяющихся вспомогательных задач
