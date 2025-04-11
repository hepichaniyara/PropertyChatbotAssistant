package com.ai.PropertyChatbotAssistant.controller;

import com.ai.PropertyChatbotAssistant.service.AppointmentService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("prop")
@AllArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;

    @PostMapping("schedule/req")
    public ResponseEntity<String> createAppointment(
            @RequestHeader("X-Session-ID") String sessionId,
            @RequestBody String req) {
        String res = appointmentService.scheduleAppointment(req, sessionId);
        return ResponseEntity.ok(res);
    }
}
