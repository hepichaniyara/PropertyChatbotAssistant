package com.ai.PropertyChatbotAssistant.cleanup;

import com.ai.PropertyChatbotAssistant.session.AppointmentSessionStore;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SessionCleanupTask {

    private final AppointmentSessionStore sessionStore;

    @Scheduled(fixedRate = 60000) // every 1 min
    public void cleanUpInactiveSessions() {
        sessionStore.clearExpiredSessions(4 * 60 * 1000); // 4 minutes
    }
}