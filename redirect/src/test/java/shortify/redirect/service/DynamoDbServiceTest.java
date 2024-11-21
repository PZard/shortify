package shortify.redirect.service;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.GetItemRequest;
import com.amazonaws.services.dynamodbv2.model.GetItemResult;
import com.amazonaws.services.dynamodbv2.model.AmazonDynamoDBException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class DynamoDbServiceTest {

    @Mock
    private AmazonDynamoDB dynamoDbClient;

    private DynamoDbService dynamoDbService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // Injetamos manualmente o mock do cliente
        dynamoDbService = new DynamoDbService() {
            @Override
            protected AmazonDynamoDB createDynamoDbClient() {
                return dynamoDbClient;
            }
        };
    }

    @Test
    void whenValidCode_thenReturnUrl() {
        // Arrange
        String code = "abc123";
        String expectedUrl = "https://www.example.com";

        Map<String, AttributeValue> item = new HashMap<>();
        item.put("url", new AttributeValue().withS(expectedUrl));

        GetItemResult mockResult = new GetItemResult().withItem(item);
        when(dynamoDbClient.getItem(any(GetItemRequest.class))).thenReturn(mockResult);

        // Act
        String result = dynamoDbService.getUrl(code);

        // Assert
        assertEquals(expectedUrl, result);
    }

    @Test
    void whenDynamoDbThrowsException_thenThrowRuntimeException() {
        // Arrange
        String code = "error";
        when(dynamoDbClient.getItem(any(GetItemRequest.class)))
                .thenThrow(new AmazonDynamoDBException("DynamoDB error"));

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () -> {
            dynamoDbService.getUrl(code);
        });

        assertTrue(exception.getMessage().contains("Failed to fetch URL from DynamoDB"));
    }

    @Test
    void whenItemNotFound_thenThrowException() {
        // Arrange
        String code = "nonexistent";
        GetItemResult mockResult = new GetItemResult().withItem(null);
        when(dynamoDbClient.getItem(any(GetItemRequest.class))).thenReturn(mockResult);

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () -> {
            dynamoDbService.getUrl(code);
        });

        assertNotNull(exception);
    }
}