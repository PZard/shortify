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
        String shortCode = "abc123";
        String expectedUrl = "https://www.google.com";
        MockHttpServletResponse response = new MockHttpServletResponse();

        when(dynamoDbService.getUrl(shortCode)).thenReturn(expectedUrl);

        urlRedirectController.redirect(shortCode, response);

        assertEquals(expectedUrl, response.getRedirectedUrl());
        verify(dynamoDbService).getUrl(shortCode);
    }

    @Test
    void shouldHandleServiceException() {
        String shortCode = "invalid";
        MockHttpServletResponse response = new MockHttpServletResponse();

        when(dynamoDbService.getUrl(shortCode))
                .thenThrow(new RuntimeException("Failed to fetch URL"));

        assertThrows(RuntimeException.class, () ->
                urlRedirectController.redirect(shortCode, response));

        verify(dynamoDbService).getUrl(shortCode);
    }
}