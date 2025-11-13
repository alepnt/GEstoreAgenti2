package com.example.common.dto;

import java.time.Instant;
import java.util.Objects;

/**
 * Messaggio di notifica generico utilizzato dall'Observer.
 */
public class NotificationMessage {

    private final String channel;
    private final String payload;
    private final Instant timestamp;

    public NotificationMessage(String channel, String payload, Instant timestamp) {
        this.channel = Objects.requireNonNull(channel, "channel");
        this.payload = Objects.requireNonNull(payload, "payload");
        this.timestamp = Objects.requireNonNull(timestamp, "timestamp");
    }

    public String getChannel() {
        return channel;
    }

    public String getPayload() {
        return payload;
    }

    public Instant getTimestamp() {
        return timestamp;
    }
}
