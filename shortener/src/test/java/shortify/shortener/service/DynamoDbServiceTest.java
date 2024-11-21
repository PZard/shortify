package shortify.shortener.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Field;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.model.AmazonDynamoDBException;
import com.amazonaws.services.dynamodbv2.model.PutItemRequest;
import com.amazonaws.services.dynamodbv2.model.PutItemResult;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DynamoDbServiceTest {

    private DynamoDbService dynamoDbService;
    private AmazonDynamoDB mockDynamoDbClient;

    @BeforeEach
    void setUp() throws Exception {
        // Cria um mock do AmazonDynamoDB
        mockDynamoDbClient = mock(AmazonDynamoDB.class);

        // Instancia a classe de serviço
        dynamoDbService = new DynamoDbService();

        // Usa reflexão para injetar o mock no campo privado "dynamoDbClient"
        Field dynamoDbClientField = DynamoDbService.class.getDeclaredField("dynamoDbClient");
        dynamoDbClientField.setAccessible(true);
        dynamoDbClientField.set(dynamoDbService, mockDynamoDbClient);
    }

    @Test
    void testSaveUrl_Success() {
        // Configura o mock para simular sucesso no método putItem
        when(mockDynamoDbClient.putItem(any(PutItemRequest.class))).thenReturn(new PutItemResult());

        // Dados de entrada
        String code = "testCode";
        String url = "http://example.com";

        // Executa o método
        dynamoDbService.saveUrl(code, url);

        // Verifica se o método putItem foi chamado com os parâmetros corretos
        verify(mockDynamoDbClient).putItem(any(PutItemRequest.class));
    }

    @Test
    void testSaveUrl_ThrowsException() {
        // Configura o mock para lançar uma AmazonDynamoDBException ao chamar putItem
        AmazonDynamoDBException dynamoDbException = new AmazonDynamoDBException("Mocked exception");
        when(mockDynamoDbClient.putItem(any(PutItemRequest.class)))
                .thenThrow(dynamoDbException);

        // Dados de entrada
        String code = "testCode";
        String url = "http://example.com";

        // Captura a exceção lançada pelo serviço
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            dynamoDbService.saveUrl(code, url);
        });

        // Valida se a mensagem gerada pelo serviço contém o esperado
        String expectedMessage = "Failed to save URL to DynamoDB";
        assertTrue(exception.getMessage().contains(expectedMessage),
                "A mensagem esperada não foi encontrada. Mensagem atual: " + exception.getMessage());
    }
}

