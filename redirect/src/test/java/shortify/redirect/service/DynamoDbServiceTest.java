package shortify.redirect.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.model.AmazonDynamoDBException;
import com.amazonaws.services.dynamodbv2.model.GetItemRequest;
import com.amazonaws.services.dynamodbv2.model.GetItemResult;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DynamoDbServiceTest {

    private DynamoDbService dynamoDbService;
    private AmazonDynamoDB mockDynamoDbClient;

    @BeforeEach
    void setUp() throws Exception {
        mockDynamoDbClient = mock(AmazonDynamoDB.class);
        dynamoDbService = new DynamoDbService();

        Field dynamoDbClientField = DynamoDbService.class.getDeclaredField("dynamoDbClient");
        dynamoDbClientField.setAccessible(true);
        dynamoDbClientField.set(dynamoDbService, mockDynamoDbClient);
    }

    @Test
    void testGetUrl_Success() {
        String code = "testCode";
        String expectedUrl = "http://example.com";

        Map<String, AttributeValue> item = new HashMap<>();
        item.put("url", new AttributeValue(expectedUrl));

        GetItemResult mockResult = new GetItemResult().withItem(item);
        when(mockDynamoDbClient.getItem(any(GetItemRequest.class))).thenReturn(mockResult);

        String result = dynamoDbService.getUrl(code);

        assertEquals(expectedUrl, result);
        verify(mockDynamoDbClient).getItem(any(GetItemRequest.class));
    }

    @Test
    void testGetUrl_NotFound() {
        String code = "nonexistentCode";
        GetItemResult mockResult = new GetItemResult().withItem(null);
        when(mockDynamoDbClient.getItem(any(GetItemRequest.class))).thenReturn(mockResult);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            dynamoDbService.getUrl(code);
        });

        String expectedMessage = "URL not found for code";
        assertTrue(exception.getMessage().contains(expectedMessage),
                "A mensagem esperada não foi encontrada. Mensagem atual: " + exception.getMessage());
    }

    @Test
    void testGetUrl_ThrowsException() {
        String code = "testCode";
        AmazonDynamoDBException dynamoDbException = new AmazonDynamoDBException("Mocked exception");
        when(mockDynamoDbClient.getItem(any(GetItemRequest.class)))
                .thenThrow(dynamoDbException);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            dynamoDbService.getUrl(code);
        });

        String expectedMessage = "Failed to get URL from DynamoDB";
        assertTrue(exception.getMessage().contains(expectedMessage),
                "A mensagem esperada não foi encontrada. Mensagem atual: " + exception.getMessage());
    }
}