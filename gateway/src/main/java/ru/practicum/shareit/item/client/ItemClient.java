package ru.practicum.shareit.item.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.item.model.ItemDto;
import ru.practicum.shareit.comment.model.CommentDto;

@Service
public class ItemClient extends BaseClient {
    private static final String API_PREFIX = "/items";

    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    public ResponseEntity<Object> findAllOwned(Long userId) {
        return get("", userId);
    }

    public ResponseEntity<Object> findById(Long id, Long userId) {
        return get("/" + id, userId);
    }

    public ResponseEntity<Object> search(String text, Long userId) {
        return get("/search?text=" + text, userId);
    }

    public ResponseEntity<Object> createItem(ItemDto item, Long userId) {
        return post("", userId, item);
    }

    public ResponseEntity<Object> updateItem(Long id, ItemDto item, Long userId) {
        return patch("/" + id, userId, item);
    }

    public ResponseEntity<Object> createComment(Long itemId, CommentDto commentDto, Long userId) {
        return post("/" + itemId + "/comment", userId, commentDto);
    }
}