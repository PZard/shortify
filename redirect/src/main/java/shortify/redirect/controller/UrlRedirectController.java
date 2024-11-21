package shortify.redirect.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletResponse;
import shortify.redirect.service.DynamoDbService;

@RestController
@RequestMapping("/")
public class UrlRedirectController {

    @Autowired
    private DynamoDbService dynamoDbService;

    @GetMapping("/{code}")
    public void redirect(@PathVariable String code, HttpServletResponse response) throws IOException {
        String originalUrl = dynamoDbService.getUrl(code);
        response.sendRedirect(originalUrl);
    }
}
