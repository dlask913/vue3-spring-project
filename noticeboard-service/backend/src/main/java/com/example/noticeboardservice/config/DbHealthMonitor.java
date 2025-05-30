package com.example.noticeboardservice.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.HealthComponent;
import org.springframework.boot.actuate.health.HealthEndpoint;
import org.springframework.boot.actuate.health.Status;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class DbHealthMonitor {

    @Autowired
    private HealthEndpoint healthEndpoint;

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
    }
}
