package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.ShareItApp;
import ru.practicum.shareit.extention.DuplicatedDataException;
import ru.practicum.shareit.extention.NotFoundException;
import ru.practicum.shareit.user.controller.UserController;
import ru.practicum.shareit.user.model.UserDto;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = ShareItApp.class)
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class UserControllerTest {
    static int userCount = 0;
    @Autowired
    private UserController userController;

    @Test
    void userControllerCreatesCorrectUser() {
        UserDto userDto = getUserDto(userCount);
        userDto = userController.create(userDto);
        assertNotNull(userDto.getId());
    }

    @Test
    void userControllerDoesNotCreateUserWithDuplicateEmail() {
        UserDto userDto = getUserDto(userCount);
        userController.create(userDto);

        DuplicatedDataException thrown = assertThrows(
                DuplicatedDataException.class,
                () -> userController.create(userDto),
                "Контроллер не выкинул исключение о дубликате email"
        );

        assertTrue(thrown.getMessage().contains("Этот email уже используется"));
    }

    @Test
    void userControllerGetsUserById() {
        UserDto userDto = getUserDto(userCount);
        userDto = userController.create(userDto);
        UserDto createdUser = userController.findById(userDto.getId());
        assertEquals(userDto, createdUser);
    }


    @Test
    void userControllerUpdatesUser() {
        UserDto userDto = getUserDto(userCount);
        userDto = userController.create(userDto);
        userDto.setName("User" + 9999);
        userDto.setEmail("user" + 9999 + "@mail.ru");
        UserDto updatedUser = userController.update(userDto.getId(), userDto);
        assertEquals(userDto, updatedUser);
    }

    @Test
    void userControllerUpdatesWithAbsentFields() {
        UserDto userDto = getUserDto(userCount);
        userDto = userController.create(userDto);
        String email = userDto.getEmail();
        userDto.setEmail(null);
        userDto.setName("User9999");
        UserDto updatedUser = userController.update(userDto.getId(), userDto);
        assertEquals(userDto.getName(), updatedUser.getName());
        assertEquals(email, updatedUser.getEmail());
    }

    @Test
    void userControllerDoesNotUpdateUserWithDuplicateEmail() {
        UserDto userDto = getUserDto(userCount);
        userDto = userController.create(userDto);
        UserDto userDto2 = getUserDto(userCount);
        UserDto createsUserDto2 = userController.create(userDto2);
        Long userId = createsUserDto2.getId();
        userDto2.setEmail(userDto.getEmail());
        DuplicatedDataException thrown = assertThrows(
                DuplicatedDataException.class,
                () -> userController.update(userId, userDto2),
                "Контроллер не выкинул исключение о дубликате email");
        assertTrue(thrown.getMessage().contains("Этот email уже используется"));
    }


    @Test
    void userControllerDoesNotDeleteUserWithWrongId() {
        assertThrows(NotFoundException.class,
                () -> userController.delete(9999L),
                "Контроллер не выкинул исключение при попытке удалить пользователя по несуществующему id");
    }

    private UserDto getUserDto(int count) {
        userCount++;
        return UserDto.builder()
                .name("User" + count)
                .email("user" + count + "@mail.ru")
                .build();
    }
}
