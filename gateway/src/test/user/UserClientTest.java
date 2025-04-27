package user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.user.client.UserClient;
import ru.practicum.shareit.user.model.UserDto;

import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserClientTest {

    @Mock
    private RestTemplate restTemplate;

    private UserClient userClient;

    private UserDto userDto;

    @BeforeEach
    void setUp() {
        RestTemplateBuilder builder = mock(RestTemplateBuilder.class);
        when(builder.build()).thenReturn(restTemplate);
        when(builder.uriTemplateHandler(any())).thenReturn(builder);
        when(builder.requestFactory(any(Supplier.class))).thenReturn(builder);

        String serverUrl = "http://localhost:8080";
        userClient = new UserClient(serverUrl, builder);

        userDto = new UserDto();
        userDto.setId(1L);
        userDto.setName("Test User");
        userDto.setEmail("test@example.com");
    }

    @Test
    void findByIdShouldCallGetWithCorrectUrl() {
        Long userId = 1L;
        String expectedUrl = "/" + userId;

        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();

        when(restTemplate.exchange(
                eq(expectedUrl),
                eq(HttpMethod.GET),
                argThat(entity ->
                        entity.getHeaders().getContentType().equals(MediaType.APPLICATION_JSON) &&
                                entity.getHeaders().getAccept().contains(MediaType.APPLICATION_JSON)
                ),
                eq(Object.class)
        )).thenReturn(expectedResponse);

        ResponseEntity<Object> actualResponse = userClient.findById(userId);

        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    void createUserShouldCallPostWithCorrectUrlAndBody() {
        String expectedUrl = "";
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok(userDto);

        when(restTemplate.exchange(
                eq(expectedUrl),
                eq(HttpMethod.POST),
                argThat(entity ->
                        entity.getBody().equals(userDto) &&
                                entity.getHeaders().getContentType().equals(MediaType.APPLICATION_JSON) &&
                                entity.getHeaders().getAccept().contains(MediaType.APPLICATION_JSON)
                ),
                eq(Object.class)
        )).thenReturn(expectedResponse);

        ResponseEntity<Object> actualResponse = userClient.createUser(userDto);

        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    void updateUserShouldCallPatchWithCorrectUrlAndBody() {
        Long userId = 1L;
        String expectedUrl = "/" + userId;
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok(userDto);

        when(restTemplate.exchange(
                eq(expectedUrl),
                eq(HttpMethod.PATCH),
                argThat(entity ->
                        entity.getBody().equals(userDto) &&
                                entity.getHeaders().getContentType().equals(MediaType.APPLICATION_JSON) &&
                                entity.getHeaders().getAccept().contains(MediaType.APPLICATION_JSON)
                ),
                eq(Object.class)
        )).thenReturn(expectedResponse);

        ResponseEntity<Object> actualResponse = userClient.updateUser(userId, userDto);

        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    void deleteUserShouldCallDeleteWithCorrectUrl() {
        Long userId = 1L;
        String expectedUrl = "/" + userId;
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();

        when(restTemplate.exchange(
                eq(expectedUrl),
                eq(HttpMethod.DELETE),
                argThat(entity ->
                        entity.getHeaders().getContentType().equals(MediaType.APPLICATION_JSON) &&
                                entity.getHeaders().getAccept().contains(MediaType.APPLICATION_JSON)
                ),
                eq(Object.class)
        )).thenReturn(expectedResponse);

        ResponseEntity<Object> actualResponse = userClient.deleteUser(userId);

        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    void findByIdShouldHandleErrorResponse() {
        Long userId = 1L;
        String expectedUrl = "/" + userId;
        HttpStatusCodeException exception = mock(HttpStatusCodeException.class);
        when(exception.getStatusCode()).thenReturn(HttpStatus.NOT_FOUND);
        when(exception.getResponseBodyAsByteArray()).thenReturn("Not found".getBytes());

        when(restTemplate.exchange(
                eq(expectedUrl),
                eq(HttpMethod.GET),
                argThat(entity ->
                        entity.getHeaders().getContentType().equals(MediaType.APPLICATION_JSON) &&
                                entity.getHeaders().getAccept().contains(MediaType.APPLICATION_JSON)
                ),
                eq(Object.class)
        )).thenThrow(exception);

        ResponseEntity<Object> response = userClient.findById(userId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertArrayEquals("Not found".getBytes(), (byte[]) response.getBody());
    }
}