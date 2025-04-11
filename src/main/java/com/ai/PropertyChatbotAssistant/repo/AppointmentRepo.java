package com.ai.PropertyChatbotAssistant.repo;

import com.ai.PropertyChatbotAssistant.model.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppointmentRepo extends JpaRepository<Appointment, String> {
}
