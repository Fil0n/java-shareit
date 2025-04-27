package ru.practicum.shareit.extention;

public class ValidationException extends RuntimeException {
    public ValidationException(String message) {
        super(message);
    }
}
