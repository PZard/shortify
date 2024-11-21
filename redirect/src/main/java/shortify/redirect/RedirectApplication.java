package shortify.redirect;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import shortify.redirect.service.DynamoDbService;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class RedirectApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private DynamoDbService dynamoDbService;

	@Test
	void contextLoads() {
	}

	@Test
	void whenValidCode_thenRedirect() throws Exception {
		String code = "abc123";
		String targetUrl = "https://www.example.com";
		when(dynamoDbService.getUrl(code)).thenReturn(targetUrl);


		mockMvc.perform(get("/{code}", code))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl(targetUrl));
	}

	@Test
	void whenInvalidCode_thenReturn404() throws Exception {
		String code = "invalid";
		when(dynamoDbService.getUrl(code)).thenThrow(new RuntimeException("URL not found"));

		mockMvc.perform(get("/{code}", code))
				.andExpect(status().isInternalServerError());
	}

	@Test
	void whenDynamoDbError_thenReturn500() throws Exception {
		String code = "error";
		when(dynamoDbService.getUrl(code)).thenThrow(new RuntimeException("DynamoDB error"));

		mockMvc.perform(get("/{code}", code))
				.andExpect(status().isInternalServerError());
	}
}