package shortify.redirect.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import shortify.redirect.service.DynamoDbService;

import java.net.URI;

@RestController
@RequestMapping("/")
public class UrlRedirectController {

    private final DynamoDbService dynamoDbService;

    @Autowired
    public UrlRedirectController(DynamoDbService dynamoDbService) {
        this.dynamoDbService = dynamoDbService;
    }

    @GetMapping("/{code}")
    public ResponseEntity<Void> redirectUrl(@PathVariable String code) {
        try {
            String url = dynamoDbService.getUrl(code);
            return ResponseEntity.status(HttpStatus.FOUND)
                    .location(URI.create(url))
                    .build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}