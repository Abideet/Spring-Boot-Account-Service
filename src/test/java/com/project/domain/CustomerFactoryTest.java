package com.project.domain;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import com.project.domain.Customer;
import com.project.domain.CustomerFactory;

import org.json.JSONObject;

public class CustomerFactoryTest {

    @Test
    public void testGetCustomer() {
        String json_string = "{\"id\":1,\"name\":\"Abideet\",\"email\":\"abideet@example.com\",\"password\":\"secret\"}";

        Customer customer = CustomerFactory.getCustomer(json_string);

        assertNotNull(customer, "Customer should not be null");
        assertEquals(1, customer.getId(), "Customer ID should be 1");
        assertEquals("Abideet", customer.getName(), "Customer name should be Abideet");
        assertEquals("abideet@example.com", customer.getEmail(), "Customer email should be abideet@example.com");
        assertEquals("secret", customer.getPassword(), "Customer password should be secret");
    }

    @Test
    public void testGetCustomerAsJSONString() {
        Customer customer = new Customer();
        customer.setId(1);
        customer.setName("Abideet");
        customer.setEmail("abideet@example.com");
        customer.setPassword("secret");

        String json_string = CustomerFactory.getCustomerAsJSONString(customer);
        JSONObject jo = new JSONObject(json_string);

        assertEquals(1, jo.getInt("id"), "JSON id should be 1");
        assertEquals("Abideet", jo.getString("name"), "JSON name should be Abideet");
        assertEquals("abideet@example.com", jo.getString("email"), "JSON email should be abideet@example.com");
        assertEquals("secret", jo.getString("password"), "JSON password should be secret");
    }
}
