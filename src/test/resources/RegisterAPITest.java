import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponentsBuilder;

import com.project.api.RegisterAPI;
import com.project.api.TokenAPI;
import com.project.domain.Customer;
import com.project.domain.CustomerFactory;
import com.project.domain.Token;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class RegisterAPITest {

    @Mock
    private CustomerFactory customerFactoryMock;

    @Mock
    private TokenAPI tokenAPIMock;

    @InjectMocks
    private RegisterAPI registerAPI;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testRegisterCustomer_InvalidCustomer() {
        // Arrange
        Customer invalidCustomer = new Customer();
        invalidCustomer.setId(1); // Invalid since id should be 0 for a new customer

        // Act
        ResponseEntity<?> response = registerAPI.registerCustomer(invalidCustomer, UriComponentsBuilder.newInstance());

        // Assert
        assertEquals(400, response.getStatusCodeValue(), "Should return 400 for invalid customer");
    }

    @Test
    public void testRegisterCustomer_ValidCustomer() {
        // Arrange
        Customer validCustomer = new Customer();
        validCustomer.setId(0); // New customer, so ID should be 0
        validCustomer.setName("Abideet");
        validCustomer.setEmail("abideet@example.com");

        // Mock CustomerFactory behavior
        String customerJson = "{\"id\":0, \"name\":\"Abideet\", \"email\":\"abideet@example.com\"}";
        when(CustomerFactory.getCustomerAsJSONString(validCustomer)).thenReturn(customerJson);

        // Mock TokenAPI behavior
        Token token = new Token("mock-token");
        token.setToken("mock-token");
        when(tokenAPIMock.getAppUserToken()).thenReturn(token);

        // Act
        ResponseEntity<?> response = registerAPI.registerCustomer(validCustomer, UriComponentsBuilder.newInstance());

        // Assert
        assertEquals(201, response.getStatusCodeValue(), "Should return 201 Created for valid customer");
    }

    @Test
    public void testPostNewCustomerToCustomerAPI() throws Exception {
        // Arrange
        String jsonCustomer = "{\"id\":0, \"name\":\"Abideet\", \"email\":\"abideet@example.com\"}";

        // Mock the URL and HttpURLConnection
        HttpURLConnection connectionMock = mock(HttpURLConnection.class);
        URL urlMock = mock(URL.class);
        when(urlMock.openConnection()).thenReturn(connectionMock);
        when(connectionMock.getOutputStream()).thenReturn(mock(OutputStream.class));
        when(connectionMock.getResponseCode()).thenReturn(HttpURLConnection.HTTP_CREATED);

        // Mock the TokenAPI behavior
        Token token = new Token("mock-token");
        token.setToken("mock-token");
        when(tokenAPIMock.getAppUserToken()).thenReturn(token);

        // Act
        registerAPI.postNewCustomerToCustomerAPI(jsonCustomer);

        // Assert
        verify(connectionMock).setRequestMethod("POST");
        verify(connectionMock).setRequestProperty("Content-Type", "application/json");
        verify(connectionMock).setRequestProperty("authorization", "Bearer mock-token");
        verify(connectionMock).getOutputStream();
        verify(connectionMock).disconnect();
    }
}
