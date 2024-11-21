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
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@SpringBootTest
@AutoConfigureMockMvc
public class RedirectApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private DynamoDbService dynamoDbService;

	@Test
	void contextLoads() {
		// Verifica se o contexto da aplicação carrega corretamente
	}

	@Test
	void whenValidCode_thenRedirect() throws Exception {
		// Arrange
		String code = "abc123";
		String targetUrl = "https://www.example.com";
		when(dynamoDbService.getUrl(code)).thenReturn(targetUrl);

		// Act & Assert
		mockMvc.perform(get("/{code}", code))
				.andDo(print()) // Útil para debug
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl(targetUrl))
				.andExpect(header().string("Location", targetUrl));

		verify(dynamoDbService, times(1)).getUrl(code);
	}

	@Test
	void whenInvalidCode_thenReturn404() throws Exception {
		// Arrange
		String code = "invalid";
		when(dynamoDbService.getUrl(code)).thenThrow(new RuntimeException("URL not found"));

		// Act & Assert
		mockMvc.perform(get("/{code}", code))
				.andDo(print())
				.andExpect(status().isInternalServerError());

		verify(dynamoDbService, times(1)).getUrl(code);
	}

	@Test
	void whenDynamoDbError_thenReturn500() throws Exception {
		// Arrange
		String code = "error";
		when(dynamoDbService.getUrl(code))
				.thenThrow(new RuntimeException("DynamoDB error"));

		// Act & Assert
		mockMvc.perform(get("/{code}", code))
				.andDo(print())
				.andExpect(status().isInternalServerError());

		verify(dynamoDbService, times(1)).getUrl(code);
	}

	@Test
	void whenEmptyCode_thenReturn400() throws Exception {
		// Act & Assert
		mockMvc.perform(get("/{code}", ""))
				.andDo(print())
				.andExpect(status().isNotFound());
	}

	@Test
	void whenCodeWithSpecialCharacters_thenHandleAppropriately() throws Exception {
		// Arrange
		String code = "abc@123";
		String targetUrl = "https://www.example.com";
		when(dynamoDbService.getUrl(code)).thenReturn(targetUrl);

		// Act & Assert
		mockMvc.perform(get("/{code}", code))
				.andDo(print())
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl(targetUrl));

		verify(dynamoDbService, times(1)).getUrl(code);
	}

	@Test
	void whenLongCode_thenHandleAppropriately() throws Exception {
		// Arrange
		String code = "a".repeat(100); // Código muito longo
		String targetUrl = "https://www.example.com";
		when(dynamoDbService.getUrl(code)).thenReturn(targetUrl);

		// Act & Assert
		mockMvc.perform(get("/{code}", code))
				.andDo(print())
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl(targetUrl));

		verify(dynamoDbService, times(1)).getUrl(code);
	}
}