package shortify.redirect.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import shortify.redirect.service.DynamoDbService;

import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;

class UrlRedirectControllerTest {

    @Mock
    private DynamoDbService dynamoDbService;

    private UrlRedirectController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        controller = new UrlRedirectController(dynamoDbService);
    }

    @Test
    void whenValidUrl_thenRedirect() {
        String code = "validCode";
        String expectedUrl = "http://example.com";
        when(dynamoDbService.getUrl(code)).thenReturn(expectedUrl);

        ResponseEntity<Void> response = controller.redirectUrl(code);

        assertEquals(HttpStatus.FOUND, response.getStatusCode());
        assertEquals(expectedUrl, response.getHeaders().getLocation().toString());
    }

    @Test
    void whenDynamoDbError_thenReturn500() {
        String code = "error";
        when(dynamoDbService.getUrl(code)).thenThrow(new RuntimeException("DynamoDB error"));

        ResponseEntity<Void> response = controller.redirectUrl(code);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }
}