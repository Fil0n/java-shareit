package ru.practicum.shareit.item.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.model.BookingDto;
import ru.practicum.shareit.comment.model.CommentDto;

import java.util.List;

@Data
@Builder
public class ItemDto {
    private Long id;
    @NotBlank
    private String name;
    @NotBlank
    private String description;

    @NotNull
    @JsonProperty("available")
    private Boolean isAvailable;

    private BookingDto lastBooking;
    private BookingDto nextBooking;
    private List<CommentDto> comments;

}
