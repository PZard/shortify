package shortify.shortener.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Field;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.model.AmazonDynamoDBException;
import com.amazonaws.services.dynamodbv2.model.PutItemRequest;
import com.amazonaws.services.dynamodbv2.model.PutItemResult;

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
    void testSaveUrl_Success() {
        when(mockDynamoDbClient.putItem(any(PutItemRequest.class))).thenReturn(new PutItemResult());

        String code = "testCode";
        String url = "http://example.com";

        dynamoDbService.saveUrl(code, url);
        verify(mockDynamoDbClient).putItem(any(PutItemRequest.class));
    }

    @Test
    void testSaveUrl_ThrowsException() {
        AmazonDynamoDBException dynamoDbException = new AmazonDynamoDBException("Mocked exception");
        when(mockDynamoDbClient.putItem(any(PutItemRequest.class)))
                .thenThrow(dynamoDbException);

        String code = "testCode";
        String url = "http://example.com";

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            dynamoDbService.saveUrl(code, url);
        });

        String expectedMessage = "Failed to save URL to DynamoDB";
        assertTrue(exception.getMessage().contains(expectedMessage),
                "A mensagem esperada não foi encontrada. Mensagem atual: " + exception.getMessage());
    }
}

