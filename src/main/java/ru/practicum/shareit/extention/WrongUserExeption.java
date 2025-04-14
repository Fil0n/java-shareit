package ru.practicum.shareit.extention;

public class WrongUserExeption extends RuntimeException {
    public WrongUserExeption(String message) {
        super(message);
    }
}
