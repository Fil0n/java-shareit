package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.extention.ConditionsNotMetException;
import ru.practicum.shareit.extention.DuplicatedDataException;
import ru.practicum.shareit.extention.ExceptionMessages;
import ru.practicum.shareit.extention.NotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserDto;
import ru.practicum.shareit.user.model.UserMapper;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;
    private final UserMapper userMapper;

    public UserDto read(Long id) {
        return userMapper.toUserDto(userStorage.read(id));
    }

    public UserDto create(UserDto userDto) {
        User user = userMapper.toUser(userDto);
        validate(user);
        return userMapper.toUserDto(userStorage.create(user));
    }

    public UserDto update(Long id, UserDto userDto) {
        User user = userStorage.read(id);
        if (userDto.getName() != null) {
            user.setName(userDto.getName());
        }
        if (userDto.getEmail() != null) {
            user.setEmail(userDto.getEmail());
        }

        validate(user);
        return userMapper.toUserDto(userStorage.update(user));
    }

    public void delete(Long id) {
        exists(id);
        userStorage.delete(id);
    }

    private void validate(User user) throws DuplicatedDataException {
        if (userStorage.getAll()
                .stream()
                .anyMatch(u -> u.getEmail()
                        .equals(user.getEmail()) && !Objects.equals(u.getId(), user.getId()))) {
            throw new DuplicatedDataException("Этот email уже используется");
        }
    }

    public void exists(Long userId) throws ConditionsNotMetException {
        Optional.ofNullable(userStorage.read(userId))
                .orElseThrow(() -> new NotFoundException(String.format(ExceptionMessages.USER_NOT_FOUND_ERROR, userId)));
    }

}
