package com.ats;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * ATS Resume Analyzer Application
 * Workday-style resume screening with deterministic scoring and AI-powered suggestions.
 */
@SpringBootApplication
public class AtsApplication {

    public static void main(String[] args) {
        SpringApplication.run(AtsApplication.class, args);
    }
}
