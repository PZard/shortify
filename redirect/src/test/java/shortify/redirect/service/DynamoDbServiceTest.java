package shortify.redirect.service;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.model.*;
import com.amazonaws.regions.Regions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DynamoDbServiceTest {

    @Mock
    private AmazonDynamoDB mockDynamoDbClient;

    @Mock
    private AmazonDynamoDBClientBuilder mockBuilder;

    private DynamoDbService dynamoDbService;

    @BeforeEach
    void setUp() {
        when(mockBuilder.withRegion(Regions.US_EAST_2)).thenReturn(mockBuilder);
        when(mockBuilder.build()).thenReturn(mockDynamoDbClient);

        try (MockedStatic<AmazonDynamoDBClientBuilder> mockedStatic = Mockito.mockStatic(AmazonDynamoDBClientBuilder.class)) {
            mockedStatic.when(AmazonDynamoDBClientBuilder::standard).thenReturn(mockBuilder);
            dynamoDbService = new DynamoDbService();
        }
    }

    @Test
    void shouldReturnUrlWhenCodeExists() {
        String code = "abc123";
        String expectedUrl = "https://www.google.com";

        Map<String, AttributeValue> item = new HashMap<>();
        item.put("url", new AttributeValue().withS(expectedUrl));

        GetItemResult mockResult = new GetItemResult().withItem(item);
        when(mockDynamoDbClient.getItem(any(GetItemRequest.class))).thenReturn(mockResult);

        String result = dynamoDbService.getUrl(code);

        assertEquals(expectedUrl, result);
    }

    @Test
    void shouldThrowExceptionWhenDynamoDbFails() {
        String code = "abc123";
        when(mockDynamoDbClient.getItem(any(GetItemRequest.class)))
                .thenThrow(new AmazonDynamoDBException("Failed to fetch URL from DynamoDB"));

        Exception exception = assertThrows(RuntimeException.class, () ->
                dynamoDbService.getUrl(code));

        assertTrue(exception.getMessage().contains("Failed to fetch URL from DynamoDB"));
    }

    @Test
    void shouldThrowExceptionWhenItemNotFound() {
        String code = "nonexistent";
        GetItemResult mockResult = new GetItemResult();
        when(mockDynamoDbClient.getItem(any(GetItemRequest.class))).thenReturn(mockResult);

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                dynamoDbService.getUrl(code));

        assertEquals("Cannot invoke \"java.util.Map.get(Object)\" because \"item\" is null",
                exception.getMessage());
    }
}