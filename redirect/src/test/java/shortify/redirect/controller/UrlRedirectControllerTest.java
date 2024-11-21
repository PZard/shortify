package shortify.redirect.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletResponse;
import shortify.redirect.service.DynamoDbService;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class UrlRedirectControllerTest {

    @Mock
    private DynamoDbService dynamoDbService;

    @InjectMocks
    private UrlRedirectController urlRedirectController;

    @Test
    void shouldRedirectToOriginalUrl() throws Exception {
        // Arrange
        String shortCode = "abc123";
        String expectedUrl = "https://www.google.com";
        MockHttpServletResponse response = new MockHttpServletResponse();

        when(dynamoDbService.getUrl(shortCode)).thenReturn(expectedUrl);

        // Act
        urlRedirectController.redirect(shortCode, response);

        // Assert
        assertEquals(expectedUrl, response.getRedirectedUrl());
        verify(dynamoDbService).getUrl(shortCode);
    }

    @Test
    void shouldHandleServiceException() {
        // Arrange
        String shortCode = "invalid";
        MockHttpServletResponse response = new MockHttpServletResponse();

        when(dynamoDbService.getUrl(shortCode))
                .thenThrow(new RuntimeException("Failed to fetch URL"));

        // Act & Assert
        assertThrows(RuntimeException.class, () ->
                urlRedirectController.redirect(shortCode, response));

        verify(dynamoDbService).getUrl(shortCode);
    }
}