package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.model.User;

import java.util.Collection;

public interface UserStorage {
    Collection<User> getAll();

    User read(Long id);

    User create(User user);

    User update(User user);

    void delete(Long id);
}
