package com.finn;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.finn.builder.UserBuilder;
import com.finn.controller.UserController;
import com.finn.domain.User;
import com.finn.repository.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringRunner.class)
@SpringBootTest
public class UserControllerTest {

    private MockMvc mockMvc;

    @InjectMocks
    private UserController userControllerMock;

    @Mock
    private UserRepository userRepositoryMock;

    @Before
    public void init(){
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders
                .standaloneSetup(userControllerMock)
                .build();
    }

    @Test
    public void signUpTest(){
        User user = new UserBuilder().createDefaultUserVasya();
        ObjectMapper mapper = new ObjectMapper();

        when(userRepositoryMock.findByEmail(any(String.class))).thenReturn(null);
        when(userRepositoryMock.save(any(User.class))).thenReturn(user);

        try {
            mockMvc.perform(post("/signUp")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(user)))
                    .andExpect(status().isOk());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void signUpEmeilErrorTest(){
        User user = new UserBuilder().createDefaultUserVasya();
        ObjectMapper mapper = new ObjectMapper();

        when(userRepositoryMock.findByEmail(any(String.class))).thenReturn(user);

        try {
            mockMvc.perform(post("/signUp")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(user)))
                    .andExpect(status().isConflict());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void signUpInternalErrorTest(){
        User user = new UserBuilder().createDefaultUserVasya();
        ObjectMapper mapper = new ObjectMapper();

        when(userRepositoryMock.findByEmail(any(String.class))).thenReturn(null);
        when(userRepositoryMock.save(any(User.class))).thenThrow(new RuntimeException());

        try {
            mockMvc.perform(post("/signUp")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(user)))
                    .andExpect(status().isInternalServerError());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
