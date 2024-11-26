package shortify.shortener.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;
import shortify.shortener.service.DynamoDbService;

@WebMvcTest(UrlShortenerController.class)
public class UrlShortenerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DynamoDbService dynamoDbService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testShortenUrl() throws Exception {
        String originalUrl = "https://example.com";
        UrlShortenerController.UrlBody requestBody = new UrlShortenerController.UrlBody();
        requestBody.url = originalUrl;

        doNothing().when(dynamoDbService).saveUrl(anyString(), eq(originalUrl));

        mockMvc.perform(post("/shorten")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.url").exists())
                .andExpect(jsonPath("$.url").value(org.hamcrest.Matchers.startsWith("https://98e2g80fk0.execute-api.us-east-2.amazonaws.com/prod/")));
                verify(dynamoDbService, times(1)).saveUrl(anyString(), eq(originalUrl));
    }
}
