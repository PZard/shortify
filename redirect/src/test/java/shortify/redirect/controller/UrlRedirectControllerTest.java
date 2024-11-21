package shortify.redirect.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletResponse;
import shortify.redirect.service.DynamoDbService;

import java.io.IOException;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class UrlRedirectControllerTest {

    @Mock
    private DynamoDbService dynamoDbService;

    @InjectMocks
    private UrlRedirectController controller;

    @Test
    void whenValidCode_thenRedirectSuccessfully() throws IOException {
        // Arrange
        String code = "valid";
        String expectedUrl = "https://www.example.com";
        MockHttpServletResponse response = new MockHttpServletResponse();
        when(dynamoDbService.getUrl(code)).thenReturn(expectedUrl);

        // Act
        controller.redirect(code, response);

        // Assert
        assertEquals(expectedUrl, response.getRedirectedUrl());
    }

    @Test
    void whenServiceThrowsException_thenThrowException() {
        // Arrange
        String code = "invalid";
        MockHttpServletResponse response = new MockHttpServletResponse();
        when(dynamoDbService.getUrl(code)).thenThrow(new RuntimeException("Error"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            controller.redirect(code, response);
        });
    }
}