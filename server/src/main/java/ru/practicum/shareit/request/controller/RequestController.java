package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.request.model.RequestDto;
import ru.practicum.shareit.request.service.RequestService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class RequestController {
    private final RequestService requestService;

    @PostMapping
    public RequestDto create(@RequestBody
                             @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) RequestDto requestDto,
                             @RequestHeader("X-Sharer-User-Id") Long userId) {
        return requestService.createRequest(requestDto, userId);
    }

    @GetMapping
    public List<RequestDto> findAllOwn(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return requestService.readAllOwn(userId);
    }

    @GetMapping("/{id}")
    public RequestDto findById(@PathVariable Long id,
                               @RequestHeader("X-Sharer-User-Id") Long userId) {
        return requestService.getRequestDto(id);
    }

    @GetMapping("/all")
    public List<RequestDto> findAll(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return requestService.readAll(userId);
    }
}
