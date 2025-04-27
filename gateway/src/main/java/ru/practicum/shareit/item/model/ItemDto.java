package ru.practicum.shareit.item.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ItemDto {
    private Long id;
    @NotBlank
    private String name;
    @NotBlank
    private String description;

    @NotNull
    @JsonProperty("available")
    private Boolean isAvailable;

    private Long requestId;
}
