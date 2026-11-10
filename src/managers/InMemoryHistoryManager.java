package managers;

import interfaces.HistoryManager;
import models.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//final - ссылку на список нельзя поменять + список создаётся один раз

public class InMemoryHistoryManager implements HistoryManager {

    private final Map<Integer, Node> nodeMap = new HashMap<>();
    private Node head;
    private Node tail;

    private static class Node {
        Task task;
        Node prev;
        Node next;

        Node(Task task) {
            this.task = task;
        }
    }

    private void linkLast(Task task) {
        Node newNode = new Node(task);

        if (tail == null) {
            head = newNode;
            tail = newNode;
        } else {
            tail.next = newNode;
            newNode.prev = tail;
            tail = newNode;
        }
        nodeMap.put(task.getId(), newNode);
    }

    // КАРОЧЕ ЭТУ ХЕРНЮ ПРОДОЛЖИШЬ КАК ПОЙМЕШЬ
    private void removeNode(Node node) {
        if (node == null) {
            return;
        }

        Node prev = node.prev;
        Node next = node.next;

        if (prev != null) {
            prev.next = next;
        } else {
            head = next;          // удаление узла head
        }

        if (next != null) {
            next.prev = prev;
        } else {
            tail = prev;          // удаление узла tail
        }

        node.prev = null;
        node.next = null;
    }

    private List<Task> getTasks() {
        List<Task> result = new ArrayList<>();
        Node current = head;

        while (current != null) {
            result.add(current.task);
            current = current.next;
        }

        return result;
    }

    @Override
    public void add(Task task) {
        if (task == null) {
            return;
        }

        // Если такая задача уже была удаляем старый node
        Node oldNode = nodeMap.remove(task.getId());
        if (oldNode != null) {
            removeNode(oldNode);
        }

        linkLast(task);
    }

    @Override
    public void remove(int id) {
        Node node = nodeMap.remove(id);
        if (node != null) {
            removeNode(node);
        }
    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }
}
