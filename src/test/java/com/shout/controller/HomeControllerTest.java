package com.shout.controller;

import com.shout.model.User;
import com.shout.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class HomeControllerTest {
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private UserRepository userRepository;
    
    @Test
    public void testHomePageLoads() throws Exception {
        mockMvc.perform(get("/"))
            .andExpect(status().isOk())
            .andExpect(view().name("home"));
    }
    
    @Test
    public void testGetUsers() throws Exception {
        User user = new User();
        user.setUsername("testuser");
        user.setFullName("Test User");
        user.setIsActive(true);
        
        Page<User> page = new PageImpl<>(Arrays.asList(user), PageRequest.of(0, 9), 1);
        when(userRepository.findByIsActive(true, any())).thenReturn(page);
        
        mockMvc.perform(get("/users/page/0")
                .param("size", "9"))
            .andExpect(status().isOk());
    }
}
