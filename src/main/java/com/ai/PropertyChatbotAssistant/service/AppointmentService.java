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
                "\nTenant: " + current.getTenant().getTenantName() +
                "\nLandlord: " + current.getLandlord().getLandlordName() +
                "\nThank you!";
    }

    private void mergeAppointments(Appointment base, Appointment update) {
        if (isEmpty(base.getPropertyAddress()) && !isEmpty(update.getPropertyAddress()))
            base.setPropertyAddress(update.getPropertyAddress());

        if (base.getAppointmentDate() == null && update.getAppointmentDate() != null)
            base.setAppointmentDate(update.getAppointmentDate());

        if (base.getAppointmentTime() == null && update.getAppointmentTime() != null)
            base.setAppointmentTime(update.getAppointmentTime());

        if (base.getTenant() == null) base.setTenant(update.getTenant());
        else mergeTenantContact(base.getTenant(), update.getTenant());

        if (base.getLandlord() == null) base.setLandlord(update.getLandlord());
        else mergeLandlordContact(base.getLandlord(), update.getLandlord());
    }

    private void mergeTenantContact(Appointment.TenantContactDetails base, Appointment.TenantContactDetails update) {
        if (base == null || update == null) return;

        if (isEmpty(base.getTenantName()) && !isEmpty(update.getTenantName())) base.setTenantName(update.getTenantName());
        if (isEmpty(base.getTenantEmail()) && !isEmpty(update.getTenantEmail())) base.setTenantEmail(update.getTenantEmail());
        if (isEmpty(base.getTenantPhone()) && !isEmpty(update.getTenantPhone())) base.setTenantPhone(update.getTenantPhone());
    }

    private void mergeLandlordContact(Appointment.LandlordContactDetails base, Appointment.LandlordContactDetails update) {
        if (base == null || update == null) return;

        if (isEmpty(base.getLandlordName()) && !isEmpty(update.getLandlordName())) base.setLandlordName(update.getLandlordName());
        if (isEmpty(base.getLandlordEmail()) && !isEmpty(update.getLandlordEmail())) base.setLandlordEmail(update.getLandlordEmail());
        if (isEmpty(base.getLandlordPhone()) && !isEmpty(update.getLandlordPhone())) base.setLandlordPhone(update.getLandlordPhone());
    }


    private boolean isEmpty(String value) {
        return value == null || value.trim().isEmpty() || value.equalsIgnoreCase("unknown");
    }
}