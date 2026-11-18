package managers;

// Непроверяемое исключение
public class ManagerSaveException extends RuntimeException {
    public ManagerSaveException(String message) {
        super(message);
    }

    // Непроверяемое исключение + пояснение
    public ManagerSaveException(String message, Throwable cause) {
        super(message, cause);
    }
}
