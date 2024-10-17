package com.project.api;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import com.project.api.ServiceAPI;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ServiceAPITest {

    @InjectMocks
    private ServiceAPI serviceAPI;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        ServiceAPI.count = 0;
    }

    @Test
    public void testHealthCheck() {
        // Act
        String response = serviceAPI.healthCheck();

        // Assert
        assertNotNull(response, "Response should not be null");
        assertTrue(response.contains("The Authentication service is up and running!"),
                "Response should contain the health message");
        assertTrue(response.contains("Instance: " + ServiceAPI.instanceId), "Response should contain the instance ID");

        Date date = new Date();
        String expectedDate = SimpleDateFormat.getDateTimeInstance(SimpleDateFormat.SHORT, SimpleDateFormat.FULL)
                .format(date);
        assertTrue(response.contains(expectedDate), "Response should contain the correct formatted date");

        // Check call count (it should be 1 after the first call)
        assertTrue(response.contains("CallCount: 1"), "Call count should be 1");
    }

    @Test
    public void testHealthCheck_CallCountIncrements() {
        // Act
        serviceAPI.healthCheck(); // First call
        String response = serviceAPI.healthCheck(); // Second call

        // Assert
        assertTrue(response.contains("CallCount: 2"), "Call count should be 2 after two calls");
    }
}
