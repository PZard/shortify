package shortify.redirect.service;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
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

    private DynamoDbService service;

    @BeforeEach
    void setUp() {
        service = new DynamoDbService() {
            @Override
            protected AmazonDynamoDB createDynamoDbClient() {
                return dynamoDbClient;
            }
        };
    }

    @Test
    void whenGetUrl_thenReturnCorrectUrl() {
        String code = "test";
        String expectedUrl = "https://www.example.com";

        Map<String, AttributeValue> item = new HashMap<>();
        item.put("url", new AttributeValue(expectedUrl));

        GetItemResult result = new GetItemResult().withItem(item);
        when(dynamoDbClient.getItem(any(GetItemRequest.class))).thenReturn(result);

        String actualUrl = service.getUrl(code);

        assertEquals(expectedUrl, actualUrl);
    }

    @Test
    void whenDynamoDbError_thenThrowException() {
        String code = "error";
        when(dynamoDbClient.getItem(any(GetItemRequest.class)))
                .thenThrow(new AmazonDynamoDBException("Error"));

        assertThrows(RuntimeException.class, () -> service.getUrl(code));
    }
}