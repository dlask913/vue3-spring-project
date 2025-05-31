package com.example.noticeboardservice.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.HealthComponent;
import org.springframework.boot.actuate.health.HealthEndpoint;
import org.springframework.boot.actuate.health.Status;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class DbHealthMonitor {

    @Autowired
    private HealthEndpoint healthEndpoint;
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${slack.webhook.url}")
    private String slackWebhookUrl;


    @Scheduled(fixedDelay = 10000) // 10초마다 확인
    public void checkDbHealth() {
        HealthComponent dbHealth = healthEndpoint.healthForPath("db");
        Status status = (dbHealth != null) ? dbHealth.getStatus() : Status.UNKNOWN;

        if (Status.DOWN.equals(status)) {
            sendAlert("DB is DOWN!");
        } else {
            sendAlert("DB is UP!");
        }
    }

    private void sendAlert(String message) {
        System.out.println("[ALERT] " + message);

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            String payload = String.format("{\"text\": \"%s\"}", message);
            HttpEntity<String> entity = new HttpEntity<>(payload, headers);

            restTemplate.postForEntity(slackWebhookUrl, entity, String.class);
        } catch (Exception e) {
            System.err.println("[SLACK ERROR] " + e.getMessage());
        }
    }
}
