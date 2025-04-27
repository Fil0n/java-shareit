package ru.practicum.shareit.request.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RequestDto {
    @NotBlank(message = "Описание не может быть пустым")
    private String description;
}