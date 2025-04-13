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
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserDto read(Long id) {
        return userMapper.toUserDto(exists(id));
    }

    public UserDto create(UserDto userDto) {
        User user = userMapper.toUser(userDto);
        validate(user);
        return userMapper.toUserDto(userRepository.saveAndFlush(user));
    }

    public UserDto update(Long id, UserDto userDto) {
        User user = exists(id);
        if (userDto.getName() != null) {
            user.setName(userDto.getName());
        }
        if (userDto.getEmail() != null) {
            user.setEmail(userDto.getEmail());
        }

        validate(user);
        return userMapper.toUserDto(userRepository.saveAndFlush(user));
    }

    public void delete(Long id) {
        exists(id);
        userRepository.deleteById(id);
    }

    private void validate(User user) throws DuplicatedDataException {
        if (userRepository.findAllByEmail(user.getEmail())
                .stream()
                .anyMatch(u ->
                        !Objects.equals(u.getId(), user.getId()))) {
            throw new DuplicatedDataException("Этот email уже используется");
        }
    }

    public User exists(Long userId) throws ConditionsNotMetException {
        if (userId == null) {
            throw new ConditionsNotMetException(ExceptionMessages.NOT_FOUND_ITEM);
        }

        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format(ExceptionMessages.USER_NOT_FOUND_ERROR, userId)));
    }

}
