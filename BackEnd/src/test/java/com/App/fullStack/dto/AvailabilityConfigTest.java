package com.App.fullStack.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.env.Environment;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.mockito.Mockito.when;

public class AvailabilityConfigTest {

    @InjectMocks
    private AvailabilityConfig availabilityConfig;

    @Mock
    private Environment env;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetSupplies_ProvidedValue() {
        // Arrange
        when(env.getProperty("availability.supplies", "ONHAND,PLANNED,INTRANSIT")).thenReturn("AVAILABLE,PLANNED");

        // Act
        String[] supplies = availabilityConfig.getSupplies();

        // Assert
        assertArrayEquals(new String[]{"AVAILABLE", "PLANNED"}, supplies);
    }


    @Test
    public void testGetDemands_ProvidedValue() {
        // Arrange
        when(env.getProperty("availability.demands", "CONFIRMED,HARDPROMISED")).thenReturn("NEWORDER,CONFIRMED");

        // Act
        String[] demands = availabilityConfig.getDemands();

        // Assert
        assertArrayEquals(new String[]{"NEWORDER", "CONFIRMED"}, demands);
    }


    @Test
    public void testGetExcludedLocations_WithExclusions() {
        // Arrange
        when(env.getProperty("availability.locations.exclude", "")).thenReturn("LOCATION1,LOCATION2");

        // Act
        String[] excludedLocations = availabilityConfig.getExcludedLocations();

        // Assert
        assertArrayEquals(new String[]{"LOCATION1", "LOCATION2"}, excludedLocations);
    }
}
