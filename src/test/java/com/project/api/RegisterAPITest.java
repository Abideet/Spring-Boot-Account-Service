package com.project.api;

import static org.mockito.Mockito.mockStatic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.domain.Customer;
import com.project.domain.CustomerFactory;
import com.project.domain.Token;
import com.project.api.TokenAPI;

@WebMvcTest(RegisterAPI.class)
public class RegisterAPITest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testRegisterCustomer_ValidCustomer() throws Exception {
        Customer validCustomer = new Customer();
        validCustomer.setId(0);
        validCustomer.setName("Abideet");
        validCustomer.setEmail("abideet@example.com");
        validCustomer.setPassword("secret");

        String customerJson = objectMapper.writeValueAsString(validCustomer);

        try (MockedStatic<CustomerFactory> customerFactoryMock = mockStatic(CustomerFactory.class);
                MockedStatic<TokenAPI> tokenAPIMock = mockStatic(TokenAPI.class)) {

            customerFactoryMock.when(() -> CustomerFactory.getCustomerAsJSONString(validCustomer))
                    .thenReturn(customerJson);

            Token token = new Token("mock-token");
            tokenAPIMock.when(TokenAPI::getAppUserToken).thenReturn(token);

            mockMvc.perform(post("/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(customerJson))
                    .andExpect(status().isCreated());
        }
    }

    @Test
    public void testRegisterCustomer_InvalidCustomer() throws Exception {
        Customer invalidCustomer = new Customer();
        invalidCustomer.setId(1);
        invalidCustomer.setName("Abideet");
        invalidCustomer.setEmail("abideet@example.com");
        invalidCustomer.setPassword("secret");

        String customerJson = objectMapper.writeValueAsString(invalidCustomer);

        mockMvc.perform(post("/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(customerJson))
                .andExpect(status().isBadRequest());
    }
}
