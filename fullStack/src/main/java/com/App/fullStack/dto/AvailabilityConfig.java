package com.App.fullStack.dto;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:availability.config")
public class AvailabilityConfig {

    @Value("${availability.supplies}")
    private String[] supplies;

    @Value("${availability.demands}")
    private String[] demands;

    @Value("${availability.locations.exclude}")
    private String[] excludedLocations;

    public String[] getSupplies() {
        return supplies;
    }

    public String[] getDemands() {
        return demands;
    }

    public String[] getExcludedLocations() {
        return excludedLocations;
    }
}
