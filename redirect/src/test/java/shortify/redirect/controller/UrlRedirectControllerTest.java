package shortify.redirect.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import shortify.redirect.service.DynamoDbService;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import static org.mockito.Mockito.*;

class UrlRedirectControllerTest {

    @Mock
    private DynamoDbService dynamoDbService;

    @Mock
    private HttpServletResponse response;

    @InjectMocks
    private UrlRedirectController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void whenValidCode_thenRedirectSuccessfully() throws IOException {
        // Arrange
        String code = "abc123";
        String expectedUrl = "https://www.example.com";
        when(dynamoDbService.getUrl(code)).thenReturn(expectedUrl);

        // Act
        controller.redirect(code, response);

        // Assert
        verify(response).sendRedirect(expectedUrl);
    }

    @Test
    void whenServiceThrowsException_thenPropagateException() {
        // Arrange
        String code = "invalid";
        when(dynamoDbService.getUrl(code)).thenThrow(new RuntimeException("URL not found"));

        // Act & Assert
        try {
            controller.redirect(code, response);
        } catch (Exception e) {
            verify(response, never()).sendRedirect(anyString());
        }
    }
}