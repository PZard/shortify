package shortify.redirect;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import shortify.redirect.service.DynamoDbService;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;

@SpringBootTest
@AutoConfigureMockMvc
class RedirectApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private DynamoDbService dynamoDbService;

	@Test
	void whenValidUrl_thenRedirect() throws Exception {
		String code = "validCode";
		String expectedUrl = "http://example.com";
		when(dynamoDbService.getUrl(code)).thenReturn(expectedUrl);

		mockMvc.perform(get("/" + code))
				.andExpect(status().isFound())
				.andExpect(header().string("Location", expectedUrl));
	}

	@Test
	void whenDynamoDbError_thenReturn500() throws Exception {
		String code = "error";
		when(dynamoDbService.getUrl(code)).thenThrow(new RuntimeException("DynamoDB error"));

		mockMvc.perform(get("/" + code))
				.andExpect(status().isInternalServerError());
	}
}