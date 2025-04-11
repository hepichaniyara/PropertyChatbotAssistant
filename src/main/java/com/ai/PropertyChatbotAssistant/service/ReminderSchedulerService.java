package com.ai.PropertyChatbotAssistant.service;

import com.ai.PropertyChatbotAssistant.model.Appointment;
import com.ai.PropertyChatbotAssistant.repo.AppointmentRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReminderSchedulerService {

    private final AppointmentRepo appointmentRepo;

    @Scheduled(fixedRate = 60000) // every 1 minute
    @Transactional
    public void sendAppointmentReminders() {
        LocalDateTime now = LocalDateTime.now().withSecond(0).withNano(0);
        LocalDateTime oneHourLater = now.plusHours(1);

        List<Appointment> appointments = appointmentRepo.findAll();

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

        for (Appointment appt : appointments) {
            try {
                LocalDate date = LocalDate.parse(appt.getAppointmentDate(), dateFormatter);
                LocalTime time = LocalTime.parse(appt.getAppointmentTime(), timeFormatter).withSecond(0).withNano(0);

                LocalDateTime apptDateTime = LocalDateTime.of(date, time);

                if (apptDateTime.equals(oneHourLater)) {
                    log.info("[Reminder] Appointment in 1 hour!");
                    log.info("Property: {}", appt.getPropertyAddress());
                    log.info("Tenant: {} ({}, {})",
                            appt.getTenant().getTenantName(),
                            appt.getTenant().getTenantEmail(),
                            appt.getTenant().getTenantPhone());
                    log.info("Landlord: {} ({}, {})",
                            appt.getLandlord().getLandlordName(),
                            appt.getLandlord().getLandlordEmail(),
                            appt.getLandlord().getLandlordPhone());
                    log.info("Scheduled At: {} {}", appt.getAppointmentDate(), appt.getAppointmentTime());
                }

            } catch (Exception e) {
                log.warn("Could not parse date/time for appointment ID {}: {}", appt.getId(), e.getMessage());
            }
        }
    }
}