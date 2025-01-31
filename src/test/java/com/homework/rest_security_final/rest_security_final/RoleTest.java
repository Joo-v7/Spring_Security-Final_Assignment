package com.homework.rest_security_final.rest_security_final;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@AutoConfigureMockMvc
@SpringBootTest
public class RoleTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    @WithMockUser(username="admin", roles={"ADMIN"})
    void adminSuccessTest() throws Exception{
        mockMvc.perform(get("/admin/home"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin"));
    }

    @Test
    @WithMockUser(username="member", roles={"MEMBER"})
    void adminMemberFailTest() throws Exception {
        mockMvc.perform(get("/admin/home"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @WithMockUser(username="google", roles={"GOOGLE"})
    void adminGoogleFailTest() throws Exception {
        mockMvc.perform(get("/admin/home"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @WithMockUser(username="member", roles= {"MEMBER"})
    void memberSuccessTest() throws Exception {
        mockMvc.perform(get("/member/home"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username="admin", roles= {"ADMIN"})
    void memberAdminFailTest() throws Exception {
        mockMvc.perform(get("/member/home"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @WithMockUser(username="google", roles= {"GOOGLE"})
    void memberGoogleFailTest() throws Exception {
        mockMvc.perform(get("/member/home"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @WithMockUser(username="google", roles={"GOOGLE"})
    void googleSuccessTest() throws Exception {
        mockMvc.perform(get("/google/home"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username="member", roles= {"MEMBER"})
    void googleMemberFailTest() throws Exception {
        mockMvc.perform(get("/google/home"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @WithMockUser(username="admin", roles= {"ADMIN"})
    void googleAdminFailTest() throws Exception {
        mockMvc.perform(get("/google/home"))
                .andExpect(status().is4xxClientError());
    }
}
