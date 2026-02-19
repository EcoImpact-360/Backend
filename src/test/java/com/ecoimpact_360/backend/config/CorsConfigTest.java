package com.ecoimpact_360.backend.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CorsConfigTest {

    @Autowired
    private MockMvc mockMvc;

    private static final String API_PATH = "/api/test";
    private static final String ALLOWED_ORIGIN = "http://localhost:5173";
    private static final String NOT_ALLOWED_ORIGIN = "http://malicious-site.com";

    @Test
    void preflightRequest_Returns200WithCorsHeaders() throws Exception {
        mockMvc.perform(options(API_PATH)
                        .header("Origin", ALLOWED_ORIGIN)
                        .header("Access-Control-Request-Method", "GET")
                        .header("Access-Control-Request-Headers", "Content-Type"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", ALLOWED_ORIGIN))
                .andExpect(header().string("Access-Control-Allow-Methods", "GET,POST,PUT,DELETE,OPTIONS"))
                .andExpect(header().string("Access-Control-Allow-Credentials", "true"))
                .andExpect(header().exists("Access-Control-Max-Age"));
    }

    @Test
    void getRequest_FromAllowedOrigin_ReturnsCorsHeaders() throws Exception {
        mockMvc.perform(get(API_PATH)
                        .header("Origin", ALLOWED_ORIGIN))
                .andExpect(header().string("Access-Control-Allow-Origin", ALLOWED_ORIGIN))
                .andExpect(header().string("Access-Control-Allow-Credentials", "true"));
    }

    @Test
    void optionsRequest_FromNotAllowedOrigin_ShouldDenyCorsHeaders() throws Exception {
        mockMvc.perform(options(API_PATH)
                        .header("Origin", NOT_ALLOWED_ORIGIN)
                        .header("Access-Control-Request-Method", "GET"))
                .andExpect(header().doesNotExist("Access-Control-Allow-Origin"));
    }

    @Test
    void corsHeaders_ContainAllHttpMethods() throws Exception {
        mockMvc.perform(options(API_PATH)
                        .header("Origin", ALLOWED_ORIGIN)
                        .header("Access-Control-Request-Method", "POST"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Methods", 
                        org.hamcrest.Matchers.containsString("GET")))
                .andExpect(header().string("Access-Control-Allow-Methods", 
                        org.hamcrest.Matchers.containsString("POST")))
                .andExpect(header().string("Access-Control-Allow-Methods", 
                        org.hamcrest.Matchers.containsString("PUT")))
                .andExpect(header().string("Access-Control-Allow-Methods", 
                        org.hamcrest.Matchers.containsString("DELETE")))
                .andExpect(header().string("Access-Control-Allow-Methods", 
                        org.hamcrest.Matchers.containsString("OPTIONS")));
    }

    @Test
    void corsHeaders_AllowCredentials() throws Exception {
        mockMvc.perform(options(API_PATH)
                        .header("Origin", ALLOWED_ORIGIN)
                        .header("Access-Control-Request-Method", "GET"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Credentials", "true"));
    }

    @Test
    void corsHeaders_MaxAgeIsSet() throws Exception {
        mockMvc.perform(options(API_PATH)
                        .header("Origin", ALLOWED_ORIGIN)
                        .header("Access-Control-Request-Method", "GET"))
                .andExpect(status().isOk())
                .andExpect(header().exists("Access-Control-Max-Age"));
    }

    @Test
    void apiPath_CorsEnabled() throws Exception {
        mockMvc.perform(get("/api/schools")
                        .header("Origin", ALLOWED_ORIGIN))
                .andExpect(header().exists("Access-Control-Allow-Origin"));
    }

    @Test
    void nonApiPath_CorsNotEnabled() throws Exception {
        mockMvc.perform(get("/non-api-path")
                        .header("Origin", ALLOWED_ORIGIN))
                .andExpect(header().doesNotExist("Access-Control-Allow-Origin"));
    }

    @Test
    void preflightRequest_WithoutOrigin_ShouldNotIncludeCorsHeaders() throws Exception {
        mockMvc.perform(options(API_PATH)
                        .header("Access-Control-Request-Method", "GET"))
                .andExpect(header().doesNotExist("Access-Control-Allow-Origin"));
    }

    @Test
    void corsConfiguration_AllowsContentTypeHeader() throws Exception {
        mockMvc.perform(options(API_PATH)
                        .header("Origin", ALLOWED_ORIGIN)
                        .header("Access-Control-Request-Method", "POST")
                        .header("Access-Control-Request-Headers", "Content-Type"))
                .andExpect(status().isOk())
                .andExpect(header().exists("Access-Control-Allow-Headers"));
    }
}
