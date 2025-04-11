package com.ai.PropertyChatbotAssistant.service;

import com.ai.PropertyChatbotAssistant.exception.PropertyChatbotAssistantException;
import com.ai.PropertyChatbotAssistant.model.Appointment;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class ChatAIService {

    @Value("${gemini.api.url}")
    private String geminiApiUrl;

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    private final WebClient webClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public ChatAIService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    public Appointment extractAppointmentFromMessage(String userMessage, String sessionId) {
        String prompt = "You are assisting in scheduling property viewings between landlords and tenants. " +
                "Extract the following details from the message if available "+
                "- Property address\n- Date and Time\n- Landlord and Tenant contact info (name, email, phone). " +
                "If any field is missing or unclear, set it to an empty string (\"\"). " +
                "Respond with VALID JSON ONLY, strictly adhering to this format, with no additional text, comments, or code blocks: " +
                "{\"propertyAddress\":\"\",\"appointmentDate\":\"\",\"appointmentTime\":\"\"," +
                "\"tenant\":{\"name\":\"\",\"email\":\"\",\"phone\":\"\"}," +
                "\"landlord\":{\"name\":\"\",\"email\":\"\",\"phone\":\"\"}}";

        String fullPrompt = prompt + "\n\nUser message: \"" + userMessage + "\"";

        var reqBody = Map.of(
                "contents", List.of(
                        Map.of("parts", List.of(Map.of("text", fullPrompt)))
                )
        );

        try {
            String jsonResponse = webClient.post()
                    .uri(geminiApiUrl + geminiApiKey)
                    .header("Content-Type", "application/json")
                    .bodyValue(reqBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            log.info("Raw API response: {}", jsonResponse);

            JsonNode root = objectMapper.readTree(jsonResponse);
            String responseText = root.path("candidates").get(0)
                    .path("content").path("parts").get(0)
                    .path("text").asText();

            String cleanedJson = responseText
                    .replaceAll("(?s)```json\\s*", "") // Remove ```json
                    .replaceAll("(?s)```\\s*", "")     // Remove ```
                    .trim();

            log.info("Cleaned JSON: {}", cleanedJson);

            Appointment appointment = objectMapper.readValue(cleanedJson, Appointment.class);

            if (hasMissingRequiredFields(appointment)) {
                log.warn("Incomplete data: {}", appointment);
                return appointment;
            }

            return appointment;
        } catch (Exception e) {
            log.error("Error while calling AI engine: {}", e.getMessage());
            throw new PropertyChatbotAssistantException("Failed to process the user's request.");
        }
    }

    public String askForMissingDetails(Appointment appointment) {
        StringBuilder missingDetails = new StringBuilder();

        if (isEmpty(appointment.getPropertyAddress())) {
            missingDetails.append("Property address, ");
        }
        if (isEmpty(appointment.getAppointmentDate())) {
            missingDetails.append("Appointment date, ");
        }
        if (isEmpty(appointment.getAppointmentTime())) {
            missingDetails.append("Appointment time, ");
        }
        if (appointment.getTenant() == null || isEmpty(appointment.getTenant().getName())) {
            missingDetails.append("Tenant name, ");
        }
        if (appointment.getTenant() == null || isEmpty(appointment.getTenant().getEmail())) {
            missingDetails.append("Tenant email, ");
        }
        if (appointment.getTenant() == null || isEmpty(appointment.getTenant().getPhone())) {
            missingDetails.append("Tenant phone, ");
        }
        if (appointment.getLandlord() == null || isEmpty(appointment.getLandlord().getName())) {
            missingDetails.append("Landlord name, ");
        }
        if (appointment.getLandlord() == null || isEmpty(appointment.getLandlord().getEmail())) {
            missingDetails.append("Landlord email, ");
        }
        if (appointment.getLandlord() == null || isEmpty(appointment.getLandlord().getPhone())) {
            missingDetails.append("Landlord phone, ");
        }

        if (missingDetails.length() > 0) {
            missingDetails.setLength(missingDetails.length() - 2);
            return "Please provide the following missing details: " + missingDetails.toString();
        }
        return null;
    }


    private boolean hasMissingRequiredFields(Appointment appointment) {
        return isEmpty(appointment.getPropertyAddress()) ||
                isEmpty(appointment.getAppointmentDate()) ||
                isEmpty(appointment.getAppointmentTime()) ||
                appointment.getTenant() == null ||
                isEmpty(appointment.getTenant().getName()) ||
                isEmpty(appointment.getTenant().getEmail()) ||
                isEmpty(appointment.getTenant().getPhone()) ||
                appointment.getLandlord() == null ||
                isEmpty(appointment.getLandlord().getName()) ||
                isEmpty(appointment.getLandlord().getEmail()) ||
                isEmpty(appointment.getLandlord().getPhone());
    }

    private boolean isEmpty(String value) {
        return value == null || value.trim().isEmpty() || value.equalsIgnoreCase("unknown") || value.equals("...");
    }

    public boolean isAppointmentComplete(Appointment appointment) {
        return !isEmpty(appointment.getPropertyAddress()) &&
                !isEmpty(appointment.getAppointmentDate()) &&
                !isEmpty(appointment.getAppointmentTime()) &&
                appointment.getTenant() != null &&
                !isEmpty(appointment.getTenant().getName()) &&
                !isEmpty(appointment.getTenant().getEmail()) &&
                !isEmpty(appointment.getTenant().getPhone()) &&
                appointment.getLandlord() != null &&
                !isEmpty(appointment.getLandlord().getName()) &&
                !isEmpty(appointment.getLandlord().getEmail()) &&
                !isEmpty(appointment.getLandlord().getPhone());
    }

    private void sendGreetingMessage(String sessionId) {
        String greetingMessage = "Hello and welcome! I'm here to help you schedule property viewings. How can I assist you today?";
        log.info("Greeting for session {}: {}", sessionId, greetingMessage);
    }
}