package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.request.client.RequestClient;
import ru.practicum.shareit.request.model.RequestDto;

import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemRequestClientTest {

    @Mock
    private RestTemplate restTemplate;

    private RequestClient itemRequestClient;

    private RequestDto itemRequestDto;

    @BeforeEach
    void setUp() {
        RestTemplateBuilder builder = mock(RestTemplateBuilder.class);
        when(builder.build()).thenReturn(restTemplate);
        when(builder.uriTemplateHandler(any())).thenReturn(builder);
        when(builder.requestFactory(any(Supplier.class))).thenReturn(builder);

        String serverUrl = "http://localhost:8080";
        itemRequestClient = new RequestClient(serverUrl, builder);

        itemRequestDto = new RequestDto();
        itemRequestDto.setDescription("Need item for testing");
    }

    @Test
    void createItemRequestShouldCallPostWithCorrectUrlAndBody() {
        Long userId = 1L;
        String expectedUrl = "";
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok(itemRequestDto);

        when(restTemplate.exchange(
                eq(expectedUrl),
                eq(HttpMethod.POST),
                argThat(entity ->
                        checkHeaders(entity, userId) &&
                                entity.getBody().equals(itemRequestDto)
                ),
                eq(Object.class)
        )).thenReturn(expectedResponse);

        ResponseEntity<Object> actualResponse = itemRequestClient.createItemRequest(itemRequestDto, userId);

        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    void findAllOwnShouldCallGetWithCorrectUrl() {
        Long userId = 1L;
        String expectedUrl = "";
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();

        when(restTemplate.exchange(
                eq(expectedUrl),
                eq(HttpMethod.GET),
                argThat(entity -> checkHeaders(entity, userId)),
                eq(Object.class)
        )).thenReturn(expectedResponse);

        ResponseEntity<Object> actualResponse = itemRequestClient.findAllOwn(userId);

        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    void findByIdShouldCallGetWithCorrectUrl() {
        Long requestId = 1L;
        Long userId = 1L;
        String expectedUrl = "/" + requestId;
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();

        when(restTemplate.exchange(
                eq(expectedUrl),
                eq(HttpMethod.GET),
                argThat(entity -> checkHeaders(entity, userId)),
                eq(Object.class)
        )).thenReturn(expectedResponse);

        ResponseEntity<Object> actualResponse = itemRequestClient.findById(requestId, userId);

        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    void findAllShouldCallGetWithCorrectUrl() {
        Long userId = 1L;
        String expectedUrl = "/all";
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();

        when(restTemplate.exchange(
                eq(expectedUrl),
                eq(HttpMethod.GET),
                argThat(entity -> checkHeaders(entity, userId)),
                eq(Object.class)
        )).thenReturn(expectedResponse);

        ResponseEntity<Object> actualResponse = itemRequestClient.findAll(userId);

        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    void shouldHandleErrorResponse() {
        Long requestId = 1L;
        Long userId = 1L;
        String expectedUrl = "/" + requestId;
        HttpStatusCodeException exception = mock(HttpStatusCodeException.class);
        when(exception.getStatusCode()).thenReturn(HttpStatus.NOT_FOUND);
        when(exception.getResponseBodyAsByteArray()).thenReturn("Not found".getBytes());

        when(restTemplate.exchange(
                eq(expectedUrl),
                eq(HttpMethod.GET),
                argThat(entity -> checkHeaders(entity, userId)),
                eq(Object.class)
        )).thenThrow(exception);

        ResponseEntity<Object> response = itemRequestClient.findById(requestId, userId);

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