package ru.practicum.shareit.item;


import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.ShareItApp;
import ru.practicum.shareit.extention.ConditionsNotMetException;
import ru.practicum.shareit.item.controller.ItemController;
import ru.practicum.shareit.item.model.ItemDto;
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
class ItemControllerTest {
    static int userCount = 0;
    static int itemCount = 0;

    @Autowired
    private UserController userController;

    @Autowired
    private ItemController itemController;

    @Test
    void itemControllerCreatesCorrectItem() {
        UserDto userDto = getUserDto(userCount);
        userDto = userController.create(userDto);

        ItemDto itemDto = getItemDto(itemCount);
        itemDto = itemController.create(itemDto, userDto.getId());

        assertNotNull(itemDto.getId());
    }


    @Test
    void itemControllerFindsItemById() {
        UserDto userDto = getUserDto(userCount);
        userDto = userController.create(userDto);

        ItemDto itemDto = getItemDto(itemCount);
        itemDto = itemController.create(itemDto, userDto.getId());
        ItemDto foundItemDto = itemController.findById(itemDto.getId(), userDto.getId());
        assertEquals(itemDto.getId(), foundItemDto.getId());
    }


    @Test
    void itemControllerFindsAllItemsForUser() {
        UserDto userDto = getUserDto(userCount);
        userDto = userController.create(userDto);

        ItemDto itemDto = getItemDto(itemCount);
        itemController.create(itemDto, userDto.getId());
        ItemDto itemDto2 = getItemDto(itemCount);
        itemController.create(itemDto2, userDto.getId());
        ItemDto itemDto3 = getItemDto(itemCount);
        itemController.create(itemDto3, userDto.getId());

        assertEquals(3, itemController.findAllOwned(userDto.getId()).size());
    }


    @Test
    void itemControllerUpdatesItem() {
        UserDto userDto = getUserDto(userCount);
        userDto = userController.create(userDto);

        ItemDto itemDto = getItemDto(itemCount);
        itemDto = itemController.create(itemDto, userDto.getId());
        itemDto.setName("Item9999");
        itemDto.setDescription("Desc9999");
        itemDto.setIsAvailable(false);
        ItemDto updatedItemDto = itemController.update(itemDto.getId(), itemDto, userDto.getId());
        assertEquals(itemDto, updatedItemDto);
    }


    @Test
    void itemControllerDoesNotUpdateItemForOtherUser() {
        UserDto userDto = getUserDto(userCount);
        userDto = userController.create(userDto);

        ItemDto itemDto = getItemDto(itemCount);
        ItemDto createdItemDto = itemController.create(itemDto, userDto.getId());
        Long itemId = createdItemDto.getId();
        UserDto userDto2 = getUserDto(userCount);
        userDto2 = userController.create(userDto2);
        Long userId = userDto2.getId();
        ConditionsNotMetException thrown = assertThrows(ConditionsNotMetException.class,
                () -> itemController.update(itemId, createdItemDto, userId),
                "Контроллер не выбросил исключение при попытке обновить вещь другого пользователя");
        assertTrue(thrown.getMessage().contains("Пользователь не владелец предмета"));
    }

    @Test
    void itemControllerSearchesItemsByText() {
        UserDto userDto = getUserDto(userCount);
        userDto = userController.create(userDto);

        ItemDto itemDto = getItemDto(itemCount);
        itemDto.setName("SearchItem");
        itemController.create(itemDto, userDto.getId());

        ItemDto itemDto2 = getItemDto(itemCount);
        itemDto2.setDescription("SearchItemDescription");
        itemController.create(itemDto2, userDto.getId());

        ItemDto itemDto3 = getItemDto(itemCount);
        itemDto3.setDescription("SearchItem");
        itemDto3.setDescription("SearchItemDescription");
        itemDto3.setIsAvailable(false);
        itemController.create(itemDto3, userDto.getId());

        ItemDto itemDto4 = getItemDto(itemCount);
        itemController.create(itemDto4, userDto.getId());

        assertEquals(2, itemController.search("SearchItem", userDto.getId()).size(), "Неверное количество найденных вещей");

    }

    @Test
    void itemControllerReturnsEmptyListForEmptyQuery() {
        UserDto userDto = getUserDto(userCount);
        userDto = userController.create(userDto);
        assertEquals(0, itemController.search("", userDto.getId()).size(), "Неверное количество найденных вещей");
    }

    private UserDto getUserDto(int count) {
        userCount++;
        return UserDto.builder()
                .name("User" + count)
                .email("user" + count + "@mail.ru")
                .build();
    }

    private ItemDto getItemDto(int count) {
        itemCount++;
        return ItemDto.builder()
                .name("Item" + count)
                .description("Description" + count)
                .isAvailable(true)
                .build();
    }
}
