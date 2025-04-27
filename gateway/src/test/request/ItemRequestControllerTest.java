package request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.request.client.RequestClient;
import ru.practicum.shareit.request.controller.RequestController;
import ru.practicum.shareit.request.model.RequestDto;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ItemRequestControllerTest {

    @Mock
    private RequestClient itemRequestClient;

    @InjectMocks
    private RequestController itemRequestController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(itemRequestController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void createWithValidRequestShouldReturnCreated() throws Exception {
        RequestDto requestDto = new RequestDto();
        requestDto.setDescription("Need a drill for home repairs");

        when(itemRequestClient.createItemRequest(any(RequestDto.class), anyLong()))
                .thenReturn(new ResponseEntity<>(HttpStatus.CREATED));

        mockMvc.perform(post("/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto))
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isCreated());
    }

    @Test
    void createWithInvalidRequestShouldReturnBadRequest() throws Exception {
        RequestDto requestDto = new RequestDto();

        mockMvc.perform(post("/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto))
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createWithoutUserIdHeaderShouldReturnBadRequest() throws Exception {
        RequestDto requestDto = new RequestDto();
        requestDto.setDescription("Need a drill for home repairs");

        mockMvc.perform(post("/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void findAllOwnShouldReturnOk() throws Exception {
        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk());
    }

    @Test
    void findAllOwnWithoutUserIdHeaderShouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/requests"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void findByIdShouldReturnOk() throws Exception {
        mockMvc.perform(get("/requests/1")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk());
    }

    @Test
    void findByIdWithoutUserIdHeaderShouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/requests/1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void findAllShouldReturnOk() throws Exception {
        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk());
    }

    @Test
    void findAllWithoutUserIdHeaderShouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/requests/all"))
                .andExpect(status().isBadRequest());
    }
}