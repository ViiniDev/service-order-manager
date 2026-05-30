package com.viinidev.serviceorder;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ServiceOrderFlowIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldExecuteServiceOrderLifecycleWithRoles() throws Exception {
        String clientToken = login("cliente@demo.com");
        String adminToken = login("admin@demo.com");
        String technicianToken = login("tecnico@demo.com");

        long orderId = createOrder(clientToken);

        mockMvc.perform(patch("/api/orders/{id}/assign", orderId)
                        .header("Authorization", bearer(adminToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "technicianId": 2
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.technician.email").value("tecnico@demo.com"))
                .andExpect(jsonPath("$.status").value("ASSIGNED"));

        mockMvc.perform(patch("/api/orders/{id}/status", orderId)
                        .header("Authorization", bearer(technicianToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "status": "IN_PROGRESS"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"));

        mockMvc.perform(post("/api/orders/{id}/comments", orderId)
                        .header("Authorization", bearer(technicianToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "message": "Diagnostico iniciado pelo tecnico."
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.comments.length()", greaterThanOrEqualTo(1)));

        mockMvc.perform(get("/api/dashboard")
                        .header("Authorization", bearer(adminToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total", greaterThanOrEqualTo(3)));
    }

    @Test
    void shouldPreventClientFromChangingStatus() throws Exception {
        String clientToken = login("cliente@demo.com");
        long orderId = createOrder(clientToken);

        mockMvc.perform(patch("/api/orders/{id}/status", orderId)
                        .header("Authorization", bearer(clientToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "status": "CLOSED"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Clients cannot update service order status."));
    }

    private long createOrder(String token) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/orders")
                        .header("Authorization", bearer(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "title": "Notebook sem video",
                                  "description": "Equipamento liga, mas nao exibe imagem no monitor.",
                                  "priority": "HIGH"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("OPEN"))
                .andReturn();
        return objectMapper.readTree(result.getResponse().getContentAsString()).get("id").asLong();
    }

    private String login(String email) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "%s",
                                  "password": "123456"
                                }
                                """.formatted(email)))
                .andExpect(status().isOk())
                .andReturn();
        JsonNode body = objectMapper.readTree(result.getResponse().getContentAsString());
        return body.get("token").asText();
    }

    private String bearer(String token) {
        return "Bearer " + token;
    }
}
