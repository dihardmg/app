package com.nutech.digitalservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;

@SpringBootApplication
public class DigitalServiceApplication {

    private final Environment environment;

    public DigitalServiceApplication(Environment environment) {
        this.environment = environment;
    }

    public static void main(String[] args) {
        SpringApplication.run(DigitalServiceApplication.class, args);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void logEnvironmentVariables() {
        System.out.println("=== Railway Environment Variables Check ===");
        System.out.println("DATABASE_URL: " + environment.getProperty("DATABASE_URL", "NOT SET"));
        System.out.println("DATABASE_USER: " + environment.getProperty("DATABASE_USER", "NOT SET"));
        System.out.println("DATABASE_PASSWORD: " + (environment.getProperty("DATABASE_PASSWORD") != null ? "***SET***" : "NOT SET"));
        System.out.println("DATABASE_HOST: " + environment.getProperty("DATABASE_HOST", "NOT SET"));
        System.out.println("DATABASE_PORT: " + environment.getProperty("DATABASE_PORT", "NOT SET"));
        System.out.println("DATABASE_NAME: " + environment.getProperty("DATABASE_NAME", "NOT SET"));
        System.out.println("Active Profiles: " + String.join(", ", environment.getActiveProfiles()));
        System.out.println("==========================================");
    }

}