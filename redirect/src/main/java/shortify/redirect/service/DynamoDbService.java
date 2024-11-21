package shortify.redirect.service;

import org.springframework.stereotype.Service;

import com.amazonaws.services.dynamodbv2.model.GetItemRequest;
import com.amazonaws.services.dynamodbv2.model.GetItemResult;
import com.amazonaws.services.dynamodbv2.model.AmazonDynamoDBException;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;

import java.util.HashMap;
import java.util.Map;

@Service
public class DynamoDbService {

    private final AmazonDynamoDB dynamoDbClient;
    private final String tableName = "ShortenedUrls";

    public DynamoDbService() {
        this.dynamoDbClient = createDynamoDbClient();
    }

    protected AmazonDynamoDB createDynamoDbClient() {
        return AmazonDynamoDBClientBuilder.standard()
                .withRegion(Regions.US_EAST_2)
                .build();
    }

    public String getUrl(String code) {
        Map<String, AttributeValue> key = new HashMap<>();
        key.put("code", new AttributeValue().withS(code));

        GetItemRequest getItemRequest = new GetItemRequest()
                .withTableName(tableName)
                .withKey(key);

        try {
            GetItemResult response = dynamoDbClient.getItem(getItemRequest);
            Map<String, AttributeValue> item = response.getItem();
            AttributeValue a = item.get("url");
            return a.getS();
        } catch (AmazonDynamoDBException e) {
            throw new RuntimeException("Failed to fetch URL from DynamoDB: " + e.getMessage(), e);
        }
    }
}

