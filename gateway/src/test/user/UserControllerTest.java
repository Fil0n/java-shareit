package user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.user.client.UserClient;
import ru.practicum.shareit.user.controller.UserController;
import ru.practicum.shareit.user.model.UserDto;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserClient userClient;

    @InjectMocks
    private UserController userController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    private UserDto userDto;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
        objectMapper = new ObjectMapper();

        userDto = new UserDto();
        userDto.setId(1L);
        userDto.setName("Test User");
        userDto.setEmail("test@example.com");
    }

    @Test
    void findByIdShouldCallClientAndReturnOk() throws Exception {
        mockMvc.perform(get("/users/{id}", 1L))
                .andExpect(status().isOk());

        verify(userClient, times(1)).findById(1L);
    }

    @Test
    void createShouldCallClientAndReturnOk() throws Exception {
        when(userClient.createUser(any(UserDto.class))).thenReturn(null);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isOk());

        verify(userClient, times(1)).createUser(any(UserDto.class));
    }

    @Test
    void createShouldReturnBadRequestWhenNameIsBlank() throws Exception {
        userDto.setName("");

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isBadRequest());

        verify(userClient, never()).createUser(any(UserDto.class));
    }

    @Test
    void createShouldReturnBadRequestWhenEmailIsInvalid() throws Exception {
        userDto.setEmail("invalid-email");

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isBadRequest());

        verify(userClient, never()).createUser(any(UserDto.class));
    }

    @Test
    void updateShouldCallClientAndReturnOk() throws Exception {
        when(userClient.updateUser(anyLong(), any(UserDto.class))).thenReturn(null);

        mockMvc.perform(patch("/users/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isOk());

        verify(userClient, times(1)).updateUser(eq(1L), any(UserDto.class));
    }

    @Test
    void deleteShouldCallClientAndReturnOk() throws Exception {
        mockMvc.perform(delete("/users/{id}", 1L))
                .andExpect(status().isOk());

        verify(userClient, times(1)).deleteUser(1L);
    }
}