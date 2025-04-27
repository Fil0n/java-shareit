package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.extention.ExceptionMessages;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.extention.NotFoundException;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.model.RequestDto;
import ru.practicum.shareit.request.model.RequestMapper;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.request.service.RequestService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RequestServiceTest {

    @Mock
    private RequestRepository requestRepository;

    @Mock
    private UserService userService;

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private RequestService requestService;

    private User createUser(Long id, String name, String email) {
        return User.builder()
                .id(id)
                .name(name)
                .email(email)
                .build();
    }

    private Request createRequest(Long id, User requester) {
        return Request.builder()
                .id(id)
                .description("Need item for testing")
                .requester(requester)
                .created(LocalDateTime.now())
                .build();
    }

    private Item createItem(Long id, String name, User owner, Request request) {
        return Item.builder()
                .id(id)
                .name(name)
                .description("Test item")
                .isAvailable(true)
                .owner(owner)
                .request(request)
                .build();
    }

    @Test
    void createRequest_shouldCreateNewRequest() {
        Long userId = 1L;
        User requester = createUser(userId, "Requester", "requester@email.com");

        Request savedRequest = createRequest(1L, requester);
        RequestDto requestDto = RequestMapper.toItemRequestDto(savedRequest);
        savedRequest.setDescription(requestDto.getDescription());

        when(userService.getUser(userId)).thenReturn(requester);
        when(requestRepository.saveAndFlush(any(Request.class))).thenReturn(savedRequest);

        RequestDto result = requestService.createRequest(requestDto, userId);

        assertNotNull(result);
        assertEquals(savedRequest.getId(), result.getId());
        assertEquals(requestDto.getDescription(), result.getDescription());
        verify(userService).getUser(userId);
        verify(requestRepository).saveAndFlush(any(Request.class));
    }

    @Test
    void readAllOwn_shouldReturnEmptyListWhenNoRequests() {
        Long userId = 1L;
        when(requestRepository.findAllByRequesterIdOrderByCreatedDesc(userId))
                .thenReturn(Collections.emptyList());
        when(itemRepository.findAllByRequestIdIn(anyList()))
                .thenReturn(Collections.emptyList());

        List<RequestDto> result = requestService.readAllOwn(userId);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(requestRepository).findAllByRequesterIdOrderByCreatedDesc(userId);
        verify(itemRepository).findAllByRequestIdIn(anyList());
    }

    @Test
    void readAllOwn_shouldReturnRequestsWithItems() {
        Long userId = 1L;
        User requester = createUser(userId, "Requester", "requester@email.com");
        Request request1 = createRequest(1L, requester);
        Request request2 = createRequest(2L, requester);
        User owner = createUser(3L, "Owner", "owner@email.com");
        Item item1 = createItem(1L, "Item 1", owner, request1);
        Item item2 = createItem(2L, "Item 2", owner, request1);

        when(requestRepository.findAllByRequesterIdOrderByCreatedDesc(userId))
                .thenReturn(List.of(request1, request2));
        when(itemRepository.findAllByRequestIdIn(List.of(1L, 2L)))
                .thenReturn(List.of(item1, item2));

        List<RequestDto> result = requestService.readAllOwn(userId);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals(2, result.get(0).getItems().size());
        verify(requestRepository).findAllByRequesterIdOrderByCreatedDesc(userId);
        verify(itemRepository).findAllByRequestIdIn(List.of(1L, 2L));
    }

    @Test
    void readAll_shouldReturnEmptyListWhenNoOtherRequests() {
        Long userId = 1L;
        when(requestRepository.findAllByRequesterIdNotOrderByCreatedDesc(userId))
                .thenReturn(Collections.emptyList());
        when(itemRepository.findAllByRequestIdIn(anyList()))
                .thenReturn(Collections.emptyList());

        List<RequestDto> result = requestService.readAll(userId);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(requestRepository).findAllByRequesterIdNotOrderByCreatedDesc(userId);
        verify(itemRepository).findAllByRequestIdIn(anyList());
    }

    @Test
    void readAll_shouldReturnOtherUsersRequests() {
        Long userId = 1L;
        User otherUser = createUser(2L, "Other", "other@email.com");
        Request request1 = createRequest(1L, otherUser);
        Request request2 = createRequest(2L, otherUser);
        User owner = createUser(3L, "Owner", "owner@email.com");
        Item item1 = createItem(1L, "Item 1", owner, request1);

        when(requestRepository.findAllByRequesterIdNotOrderByCreatedDesc(userId))
                .thenReturn(List.of(request1, request2));
        when(itemRepository.findAllByRequestIdIn(List.of(1L, 2L)))
                .thenReturn(List.of(item1));

        List<RequestDto> result = requestService.readAll(userId);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals(1, result.get(0).getItems().size());
        assertEquals(0, result.get(1).getItems().size());
        verify(requestRepository).findAllByRequesterIdNotOrderByCreatedDesc(userId);
        verify(itemRepository).findAllByRequestIdIn(List.of(1L, 2L));
    }

    @Test
    void getRequestDto_shouldReturnRequestWithItems() {
        // Given
        Long requestId = 1L;
        User requester = createUser(1L, "Requester", "requester@email.com");
        Request request = createRequest(requestId, requester);
        User owner = createUser(2L, "Owner", "owner@email.com");
        Item item1 = createItem(1L, "Item 1", owner, request);
        Item item2 = createItem(2L, "Item 2", owner, request);

        when(requestRepository.findById(requestId))
                .thenReturn(Optional.of(request));
        when(itemRepository.findAllByRequestIdIn(List.of(requestId)))
                .thenReturn(List.of(item1, item2));

        RequestDto result = requestService.getRequestDto(requestId);

        assertNotNull(result);
        assertEquals(requestId, result.getId());
        assertEquals(2, result.getItems().size());
    }


    @Test
    void getRequest_shouldReturnRequestWhenFound() {
        // Given
        Long requestId = 1L;
        User requester = createUser(1L, "Requester", "requester@email.com");
        Request request = createRequest(requestId, requester);

        when(requestRepository.findById(requestId))
                .thenReturn(Optional.of(request));

        Request result = requestService.getRequest(requestId);

        assertNotNull(result);
        assertEquals(requestId, result.getId());
        verify(requestRepository).findById(requestId);
    }
}