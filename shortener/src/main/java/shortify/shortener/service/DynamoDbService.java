package shortify.shortener.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.PutItemRequest;
import com.amazonaws.services.dynamodbv2.model.AmazonDynamoDBException;

@Service
public class DynamoDbService {
	private final AmazonDynamoDB dynamoDbClient;
    private final String tableName = "ShortenedUrls";

    public DynamoDbService() {
        this.dynamoDbClient = AmazonDynamoDBClientBuilder.standard().withRegion(Regions.US_EAST_2).build();
    }

	public void saveUrl(String code, String url) {
		Map<String, AttributeValue> item = new HashMap<>();
		item.put("code", new AttributeValue().withS(code));
		item.put("url", new AttributeValue().withS(url));

        PutItemRequest putItemRequest = new PutItemRequest()
                .withTableName(tableName)
				.withItem(item);

        try {
            dynamoDbClient.putItem(putItemRequest);
        } catch (AmazonDynamoDBException e) {
            throw new RuntimeException("Failed to save URL to DynamoDB: " + e.getMessage(), e);
        }
    }
}
