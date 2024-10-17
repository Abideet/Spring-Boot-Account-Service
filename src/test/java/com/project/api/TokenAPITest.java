package com.project.api;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.project.api.TokenAPI;
import com.project.domain.Customer;
import com.project.domain.Token;
import com.project.util.JWTHelper;
import java.net.HttpURLConnection;

public class TokenAPITest {

    @InjectMocks
    private TokenAPI tokenAPI;

    @Mock
    private JWTHelper jwtHelper;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetAll() {
        String response = tokenAPI.getAll();
        assertEquals("jwt-fake-token-asdfasdfasfa", response);
    }

    @Test
    public void testCreateTokenForCustomer_InvalidCustomer() {
        Customer invalidCustomer = new Customer();
        invalidCustomer.setName("Abideet");
        invalidCustomer.setPassword("wrongpassword");

        ResponseEntity<?> response = tokenAPI.createTokenForCustomer(invalidCustomer);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    public void testGetAppUserToken() {
        when(jwtHelper.createToken(anyString())).thenReturn("mock-token");
        Token token = tokenAPI.getAppUserToken();
        assertNotNull(token);
        assertEquals("mock-token", token.getToken());
    }

    @Test
    public void testCheckPassword_Valid() {
        Customer customer = new Customer();
        customer.setName("Abideet");
        customer.setPassword("secret");
        TokenAPI.appUserToken = new Token("mock-token");

        TokenAPI tokenAPISpy = spy(tokenAPI);
        doReturn(customer).when(tokenAPISpy).getCustomerByNameFromCustomerAPI("Abideet");

        boolean isValid = tokenAPISpy.checkPassword("Abideet", "secret");
        assertTrue(isValid);
    }

    @Test
    public void testCheckPassword_Invalid() {
        Customer customer = new Customer();
        customer.setName("Abideet");
        customer.setPassword("wrongpassword");
        TokenAPI.appUserToken = new Token("mock-token");

        TokenAPI tokenAPISpy = spy(tokenAPI);
        doReturn(customer).when(tokenAPISpy).getCustomerByNameFromCustomerAPI("Abideet");

        boolean isValid = tokenAPISpy.checkPassword("Abideet", "secret");
        assertFalse(isValid);
    }

    @Test
    public void testGetCustomerByNameFromCustomerAPI() throws Exception {
        HttpURLConnection conn = mock(HttpURLConnection.class);
        when(conn.getResponseCode()).thenReturn(HttpURLConnection.HTTP_OK);
        when(conn.getInputStream()).thenReturn(
                new java.io.ByteArrayInputStream("{\"name\":\"Abideet\",\"password\":\"secret\"}".getBytes()));

        TokenAPI tokenAPISpy = spy(tokenAPI);
        doReturn(conn).when(tokenAPISpy).getAppUserToken();

        Customer customer = tokenAPISpy.getCustomerByNameFromCustomerAPI("Abideet");
        assertNotNull(customer);
        assertEquals("Abideet", customer.getName());
        assertEquals("secret", customer.getPassword());
    }
}
