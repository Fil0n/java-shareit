package ru.practicum.shareit.user.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> userStorage = new HashMap<>();
    private Long currentId = 0L;

    @Override
    public Collection<User> getAll() {
        return userStorage.values();
    }

    @Override
    public User read(Long id) {
        return userStorage.get(id);
    }

    @Override
    public User create(User user) {
        userStorage.put(++currentId, user);
        user.setId(currentId);
        return user;
    }

    @Override
    public User update(User user) {
        userStorage.put(user.getId(), user);
        return user;
    }

    @Override
    public void delete(Long id) {
        userStorage.remove(id);
    }
}
