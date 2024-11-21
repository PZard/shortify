package shortify.shortener.controller;

import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import shortify.shortener.service.DynamoDbService;

@RestController
@RequestMapping("/shorten")
public class UrlShortenerController {

	@Autowired
    private DynamoDbService dynamoDbService;

	public static class UrlBody {
		public String url;
	}

    @PostMapping
    public ResponseEntity<UrlBody> shortenUrl(@RequestBody UrlBody request) {
        String originalUrl = request.url;
        String shortenedCode = generateRandomCode();

        dynamoDbService.saveUrl(shortenedCode, originalUrl);
        
		String shortenedUrl = "https://short.ly/" + shortenedCode;
		UrlBody responseBody = new UrlBody();
		responseBody.url = shortenedUrl;
	
        return ResponseEntity.ok(responseBody);
    }

    private String generateRandomCode() {
        String chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();

		int stringLength = 6;

        StringBuilder randomString = new StringBuilder(stringLength);
        for (int i = 0; i < stringLength; i++) {
            randomString.append(chars.charAt(random.nextInt(chars.length())));
        }

		return randomString.toString();
    }
}
