package com.App.fullStack.dto;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.beans.factory.annotation.Autowired;

@Configuration
@PropertySource("classpath:availability.config")
public class AvailabilityConfig {

    @Autowired
    private Environment env;

    private String[] getPropertyOrDefault(String propertyName, String defaultValue) {
        String value = env.getProperty(propertyName, defaultValue);
        return value.split(",");
    }

    public String[] getSupplies() {
        return getPropertyOrDefault("availability.supplies", "ONHAND,PLANNED,INTRANSIT");
    }

    public String[] getDemands() {
        return getPropertyOrDefault("availability.demands", "CONFIRMED,HARDPROMISED");
    }

    public String[] getExcludedLocations() {
        return getPropertyOrDefault("availability.locations.exclude", "");
    }
}
