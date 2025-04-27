package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.extention.ExceptionMessages;
import ru.practicum.shareit.extention.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemDto;
import ru.practicum.shareit.item.model.ItemMapper;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.model.RequestDto;
import ru.practicum.shareit.request.model.RequestMapper;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class RequestService {
    private final RequestRepository requestRepository;
    private final UserService userService;
    private final ItemRepository itemRepository;

    public RequestDto createRequest(RequestDto requestDto, Long userId) {
        User user = userService.getUser(userId);
        Request request = RequestMapper.toItemRequest(requestDto, user);
        request = requestRepository.saveAndFlush(request);
        return RequestMapper.toItemRequestDto(request);
    }

    public List<RequestDto> readAll(Long userId) {
        userService.getUser(userId);
        List<Request> requests = requestRepository.findAllByRequesterIdNotOrderByCreatedDesc(userId);
        return getRequestsWithItems(requests);
    }

    public RequestDto getRequestDto(Long id) {
        Request request = getRequest(id);
        List<Item> items = itemRepository.findAllByRequestIdIn(List.of(request.getId()));
        return RequestMapper.toItemRequestDto(getRequest(id), items.stream().map(ItemMapper::toItemDto).toList());
    }

    public Request getRequest(Long id) {
        return Optional.ofNullable(requestRepository.findById(id))
                .orElseThrow(() -> new NotFoundException(String.format(ExceptionMessages.REQUEST_NOT_FOUND_ERROR, id))).get();
    }

    public List<RequestDto> readAllOwn(Long userId) {
        userService.getUser(userId);
        List<Request> requests = requestRepository.findAllByRequesterIdOrderByCreatedDesc(userId);
        return getRequestsWithItems(requests);
    }

    private List<RequestDto> getRequestsWithItems(List<Request> requests) {
        List<Long> requestIds = requests.stream()
                .map(Request::getId)
                .toList();

        Map<Long, List<ItemDto>> itemsByRequestId = itemRepository.findAllByRequestIdIn(requestIds)
                .stream()
                .collect(Collectors.groupingBy(
                        item -> item.getRequest().getId(),
                        Collectors.mapping(ItemMapper::toItemDto, Collectors.toList())
                ));

        return requests.stream()
                .map(request -> RequestMapper.toItemRequestDto(
                        request,
                        itemsByRequestId.getOrDefault(request.getId(), Collections.emptyList())
                ))
                .toList();
    }

}
