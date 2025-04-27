package item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.comment.model.CommentDto;
import ru.practicum.shareit.item.client.ItemClient;
import ru.practicum.shareit.item.model.ItemDto;

import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemClientTest {

    @Mock
    private RestTemplate restTemplate;

    private ItemClient itemClient;

    private ItemDto itemDto;
    private CommentDto commentDto;

    @BeforeEach
    void setUp() {
        RestTemplateBuilder builder = mock(RestTemplateBuilder.class);
        when(builder.build()).thenReturn(restTemplate);
        when(builder.uriTemplateHandler(any())).thenReturn(builder);
        when(builder.requestFactory(any(Supplier.class))).thenReturn(builder);

        String serverUrl = "http://localhost:8080";
        itemClient = new ItemClient(serverUrl, builder);

        itemDto = new ItemDto();
        itemDto.setName("Test Item");
        itemDto.setDescription("Test Description");
        itemDto.setIsAvailable(true);

        commentDto = new CommentDto();
        commentDto.setText("Test Comment");
    }

    @Test
    void findAllOwnedShouldCallGetWithCorrectUrl() {
        Long userId = 1L;
        String expectedUrl = "";
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();

        when(restTemplate.exchange(
                eq(expectedUrl),
                eq(HttpMethod.GET),
                argThat(entity -> checkHeaders(entity, userId)),
                eq(Object.class)
        )).thenReturn(expectedResponse);

        ResponseEntity<Object> actualResponse = itemClient.findAllOwned(userId);

        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    void findByIdShouldCallGetWithCorrectUrl() {
        Long itemId = 1L;
        Long userId = 1L;
        String expectedUrl = "/" + itemId;
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();

        when(restTemplate.exchange(
                eq(expectedUrl),
                eq(HttpMethod.GET),
                argThat(entity -> checkHeaders(entity, userId)),
                eq(Object.class)
        )).thenReturn(expectedResponse);

        ResponseEntity<Object> actualResponse = itemClient.findById(itemId, userId);

        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    void searchShouldCallGetWithCorrectUrl() {
        String searchText = "test";
        Long userId = 1L;
        String expectedUrl = "/search?text=" + searchText;
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();

        when(restTemplate.exchange(
                eq(expectedUrl),
                eq(HttpMethod.GET),
                argThat(entity -> checkHeaders(entity, userId)),
                eq(Object.class)
        )).thenReturn(expectedResponse);

        ResponseEntity<Object> actualResponse = itemClient.search(searchText, userId);

        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    void createItemShouldCallPostWithCorrectUrlAndBody() {
        Long userId = 1L;
        String expectedUrl = "";
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok(itemDto);

        when(restTemplate.exchange(
                eq(expectedUrl),
                eq(HttpMethod.POST),
                argThat(entity ->
                        checkHeaders(entity, userId) &&
                                entity.getBody().equals(itemDto)
                ),
                eq(Object.class)
        )).thenReturn(expectedResponse);

        ResponseEntity<Object> actualResponse = itemClient.createItem(itemDto, userId);

        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    void updateItemShouldCallPatchWithCorrectUrlAndBody() {
        Long itemId = 1L;
        Long userId = 1L;
        String expectedUrl = "/" + itemId;
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok(itemDto);

        when(restTemplate.exchange(
                eq(expectedUrl),
                eq(HttpMethod.PATCH),
                argThat(entity ->
                        checkHeaders(entity, userId) &&
                                entity.getBody().equals(itemDto)
                ),
                eq(Object.class)
        )).thenReturn(expectedResponse);

        ResponseEntity<Object> actualResponse = itemClient.updateItem(itemId, itemDto, userId);

        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    void createCommentShouldCallPostWithCorrectUrlAndBody() {
        Long itemId = 1L;
        Long userId = 1L;
        String expectedUrl = "/" + itemId + "/comment";
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok(commentDto);

        when(restTemplate.exchange(
                eq(expectedUrl),
                eq(HttpMethod.POST),
                argThat(entity ->
                        checkHeaders(entity, userId) &&
                                entity.getBody().equals(commentDto)
                ),
                eq(Object.class)
        )).thenReturn(expectedResponse);

        ResponseEntity<Object> actualResponse = itemClient.createComment(itemId, commentDto, userId);

        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    void shouldHandleErrorResponse() {
        Long itemId = 1L;
        Long userId = 1L;
        String expectedUrl = "/" + itemId;
        HttpStatusCodeException exception = mock(HttpStatusCodeException.class);
        when(exception.getStatusCode()).thenReturn(HttpStatus.NOT_FOUND);
        when(exception.getResponseBodyAsByteArray()).thenReturn("Not found".getBytes());

        when(restTemplate.exchange(
                eq(expectedUrl),
                eq(HttpMethod.GET),
                argThat(entity -> checkHeaders(entity, userId)),
                eq(Object.class)
        )).thenThrow(exception);

        ResponseEntity<Object> response = itemClient.findById(itemId, userId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertArrayEquals("Not found".getBytes(), (byte[]) response.getBody());
    }

    private boolean checkHeaders(HttpEntity<?> entity, Long userId) {
        HttpHeaders headers = entity.getHeaders();
        return headers.getContentType().equals(MediaType.APPLICATION_JSON) &&
                headers.getAccept().contains(MediaType.APPLICATION_JSON) &&
                headers.getFirst("X-Sharer-User-Id").equals(userId.toString());
    }
}