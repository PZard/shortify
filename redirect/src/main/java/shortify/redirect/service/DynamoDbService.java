package shortify.redirect.service;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.GetItemRequest;
import com.amazonaws.services.dynamodbv2.model.GetItemResult;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class DynamoDbService {

    private AmazonDynamoDB dynamoDbClient;

    public String getUrl(String code) {
        try {
            Map<String, AttributeValue> key = new HashMap<>();
            key.put("code", new AttributeValue(code));

            GetItemRequest request = new GetItemRequest()
                    .withTableName("urls")
                    .withKey(key);

            GetItemResult result = dynamoDbClient.getItem(request);

            if (result.getItem() == null || !result.getItem().containsKey("url")) {
                throw new RuntimeException("URL not found for code: " + code);
            }

            return result.getItem().get("url").getS();
        } catch (Exception e) {
            throw new RuntimeException("Failed to get URL from DynamoDB: " + e.getMessage());
        }
    }
}