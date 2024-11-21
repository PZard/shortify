package shortify.redirect.service;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.InjectMocks;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class DynamoDbServiceTest {

    @Mock
    private AmazonDynamoDB dynamoDbClient;

    @InjectMocks
    private DynamoDbService dynamoDbService;

    @Test
    void shouldReturnUrlWhenCodeExists() {
        // Arrange
        String code = "abc123";
        String expectedUrl = "https://www.google.com";

        Map<String, AttributeValue> item = new HashMap<>();
        item.put("url", new AttributeValue().withS(expectedUrl));

        GetItemResult mockResult = new GetItemResult().withItem(item);
        when(dynamoDbClient.getItem(any())).thenReturn(mockResult);

        // Act
        String result = dynamoDbService.getUrl(code);

        // Assert
        assertEquals(expectedUrl, result);
        verify(dynamoDbClient).getItem(any());
    }

    @Test
    void shouldThrowExceptionWhenDynamoDbFails() {
        // Arrange
        String code = "abc123";
        when(dynamoDbClient.getItem(any()))
                .thenThrow(new AmazonDynamoDBException("Failed to fetch URL from DynamoDB"));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                dynamoDbService.getUrl(code));

        assertTrue(exception.getMessage().contains("Failed to fetch URL from DynamoDB"));
        verify(dynamoDbClient).getItem(any());
    }

    @Test
    void shouldThrowExceptionWhenItemNotFound() {
        // Arrange
        String code = "nonexistent";
        GetItemResult mockResult = new GetItemResult().withItem(null);
        when(dynamoDbClient.getItem(any())).thenReturn(mockResult);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                dynamoDbService.getUrl(code));

        assertTrue(exception.getMessage().contains("URL not found for code"));
        verify(dynamoDbClient).getItem(any());
    }
}