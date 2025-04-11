package com.ai.PropertyChatbotAssistant.service;

import com.ai.PropertyChatbotAssistant.model.Appointment;
import com.ai.PropertyChatbotAssistant.repo.AppointmentRepo;
import com.ai.PropertyChatbotAssistant.session.AppointmentSessionStore;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class AppointmentService {

    private final AppointmentRepo appointmentRepo;
    private final ChatAIService chatAIService;
    private final AppointmentSessionStore sessionStore;

    public String scheduleAppointment(String userMessage, String sessionId) {
        Appointment current = sessionStore.getOrCreateAppointment(sessionId);
        Appointment extracted = chatAIService.extractAppointmentFromMessage(userMessage, sessionId);
        log.info("Extracted appointment: {}", extracted);

        mergeAppointments(current, extracted);
        sessionStore.updateAppointment(sessionId, current);
        sessionStore.refreshSessionTime(sessionId);

        if (!chatAIService.isAppointmentComplete(current)) {
            String missing = chatAIService.askForMissingDetails(current);
            return missing != null ? missing : "Please continue providing the remaining info.";
        }

        current.setConfirmed(true);
        appointmentRepo.save(current);
        log.info("Saved appointment: {}", current);

        sessionStore.clearSession(sessionId);

        return "Your appointment has been successfully scheduled: " +
                "\nProperty: " + current.getPropertyAddress() +
                "\nDate: " + current.getAppointmentDate() +
                "\nTime: " + current.getAppointmentTime() +
                "\nTenant: " + current.getTenant().getName() +
                "\nLandlord: " + current.getLandlord().getName() +
                "\nThank you!";
    }

    private void mergeAppointments(Appointment base, Appointment update) {
        if (isEmpty(base.getPropertyAddress()) && !isEmpty(update.getPropertyAddress()))
            base.setPropertyAddress(update.getPropertyAddress());

        if (isEmpty(base.getAppointmentDate()) && !isEmpty(update.getAppointmentDate()))
            base.setAppointmentDate(update.getAppointmentDate());

        if (isEmpty(base.getAppointmentTime()) && !isEmpty(update.getAppointmentTime()))
            base.setAppointmentTime(update.getAppointmentTime());

        if (base.getTenant() == null) base.setTenant(update.getTenant());
        else mergeContact(base.getTenant(), update.getTenant());

        if (base.getLandlord() == null) base.setLandlord(update.getLandlord());
        else mergeContact(base.getLandlord(), update.getLandlord());
    }

    private void mergeContact(Appointment.ContactDetails base, Appointment.ContactDetails update) {
        if (base == null || update == null) return;

        if (isEmpty(base.getName()) && !isEmpty(update.getName())) base.setName(update.getName());
        if (isEmpty(base.getEmail()) && !isEmpty(update.getEmail())) base.setEmail(update.getEmail());
        if (isEmpty(base.getPhone()) && !isEmpty(update.getPhone())) base.setPhone(update.getPhone());
    }

    private boolean isEmpty(String value) {
        return value == null || value.trim().isEmpty() || value.equalsIgnoreCase("unknown");
    }
}