package booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.booking.client.BookingClient;
import ru.practicum.shareit.booking.model.BookingDto;
import ru.practicum.shareit.booking.model.BookingState;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingClientTest {

    @Mock
    private RestTemplate restTemplate;

    private BookingClient bookingClient;

    private BookingDto bookingDto;

    @BeforeEach
    void setUp() {
        RestTemplateBuilder builder = mock(RestTemplateBuilder.class);
        when(builder.build()).thenReturn(restTemplate);
        when(builder.uriTemplateHandler(any())).thenReturn(builder);
        when(builder.requestFactory(any(Supplier.class))).thenReturn(builder);

        String serverUrl = "http://localhost:8080";
        bookingClient = new BookingClient(serverUrl, builder);

        bookingDto = new BookingDto();
        bookingDto.setItemId(1L);
        bookingDto.setStart(LocalDateTime.now().plusDays(1));
        bookingDto.setEnd(LocalDateTime.now().plusDays(2));
    }

    @Test
    void findByIdShouldCallGetWithCorrectUrl() {
        Long userId = 1L;
        Long bookingId = 1L;
        String expectedUrl = "/" + bookingId;
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();

        when(restTemplate.exchange(
                eq(expectedUrl),
                eq(HttpMethod.GET),
                argThat(entity -> checkHeaders(entity, userId)),
                eq(Object.class)
        )).thenReturn(expectedResponse);

        ResponseEntity<Object> actualResponse = bookingClient.findById(userId, bookingId);

        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    void findAllByBookerAndStateShouldCallGetWithCorrectUrlAndParams() {
        Long userId = 1L;
        BookingState state = BookingState.ALL;
        String expectedUrl = "?state={state}";
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();

        when(restTemplate.exchange(
                eq(expectedUrl),
                eq(HttpMethod.GET),
                argThat(entity -> checkHeaders(entity, userId)),
                eq(Object.class),
                eq(Map.of("state", state.name()))
        )).thenReturn(expectedResponse);

        ResponseEntity<Object> actualResponse = bookingClient.findAllByBookerAndState(userId, state);

        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    void findAllByOwnerAndStateShouldCallGetWithCorrectUrlAndParams() {
        Long userId = 1L;
        BookingState state = BookingState.CURRENT;
        String expectedUrl = "/owner?state={state}";
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();

        when(restTemplate.exchange(
                eq(expectedUrl),
                eq(HttpMethod.GET),
                argThat(entity -> checkHeaders(entity, userId)),
                eq(Object.class),
                eq(Map.of("state", state.name()))
        )).thenReturn(expectedResponse);

        ResponseEntity<Object> actualResponse = bookingClient.findAllByOwnerAndState(userId, state);

        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    void createBookingShouldCallPostWithCorrectUrlAndBody() {
        Long userId = 1L;
        String expectedUrl = "";
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();

        when(restTemplate.exchange(
                eq(expectedUrl),
                eq(HttpMethod.POST),
                argThat(entity ->
                        checkHeaders(entity, userId) &&
                                entity.getBody().equals(bookingDto)
                ),
                eq(Object.class)
        )).thenReturn(expectedResponse);

        ResponseEntity<Object> actualResponse = bookingClient.createBooking(userId, bookingDto);

        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    void updateStatusShouldCallPatchWithCorrectUrl() {
        Long userId = 1L;
        Long bookingId = 1L;
        Boolean approved = true;
        String expectedUrl = "/" + bookingId + "?approved=" + approved;
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();

        when(restTemplate.exchange(
                eq(expectedUrl),
                eq(HttpMethod.PATCH),
                argThat(entity -> checkHeaders(entity, userId)),
                eq(Object.class)
        )).thenReturn(expectedResponse);

        ResponseEntity<Object> actualResponse = bookingClient.updateStatus(userId, bookingId, approved);

        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    void shouldHandleErrorResponse() {
        Long userId = 1L;
        Long bookingId = 1L;
        String expectedUrl = "/" + bookingId;
        HttpStatusCodeException exception = mock(HttpStatusCodeException.class);
        when(exception.getStatusCode()).thenReturn(HttpStatus.NOT_FOUND);
        when(exception.getResponseBodyAsByteArray()).thenReturn("Not found".getBytes());

        when(restTemplate.exchange(
                eq(expectedUrl),
                eq(HttpMethod.GET),
                argThat(entity -> checkHeaders(entity, userId)),
                eq(Object.class)
        )).thenThrow(exception);

        ResponseEntity<Object> response = bookingClient.findById(userId, bookingId);

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