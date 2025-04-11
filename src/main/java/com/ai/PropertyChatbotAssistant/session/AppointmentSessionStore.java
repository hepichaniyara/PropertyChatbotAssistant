package com.ai.PropertyChatbotAssistant.session;

import com.ai.PropertyChatbotAssistant.model.Appointment;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class AppointmentSessionStore {

    @Getter
    @Setter
    public static class SessionData {
        private Appointment appointment;
        private long lastUpdatedTime;

        public SessionData(Appointment appointment) {
            this.appointment = appointment;
            this.lastUpdatedTime = System.currentTimeMillis();
        }

        public void refreshTime() {
            this.lastUpdatedTime = System.currentTimeMillis();
        }
    }

    private final Map<String, SessionData> sessions = new ConcurrentHashMap<>();

    public Appointment getOrCreateAppointment(String sessionId) {
        return sessions.computeIfAbsent(sessionId, id -> new SessionData(new Appointment()))
                .getAppointment();
    }

    public void updateAppointment(String sessionId, Appointment appointment) {
        sessions.put(sessionId, new SessionData(appointment));
    }

    public void refreshSessionTime(String sessionId) {
        SessionData data = sessions.get(sessionId);
        if (data != null) data.refreshTime();
    }

    public void clearSession(String sessionId) {
        sessions.remove(sessionId);
    }

    public void clearExpiredSessions(long timeoutMillis) {
        long now = System.currentTimeMillis();
        sessions.entrySet().removeIf(entry ->
                now - entry.getValue().getLastUpdatedTime() > timeoutMillis
        );
    }
}

