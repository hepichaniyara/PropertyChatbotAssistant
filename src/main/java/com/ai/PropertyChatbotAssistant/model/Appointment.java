package com.ai.PropertyChatbotAssistant.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.Id;

@Data
@Entity
@Table(name = "appointments")
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonProperty("propertyAddress")
    @Column(name = "property_address", nullable = false)
    private String propertyAddress;

    @JsonProperty("appointmentDate")
    @Column(name = "appointment_date", nullable = false)
    private String appointmentDate;

    @JsonProperty("appointmentTime")
    @Column(name = "appointment_time", nullable = false)
    private String appointmentTime;

    @JsonProperty("tenant")
    @Embedded
    private ContactDetails tenant;

    @JsonProperty("landlord")
    @Embedded
    private ContactDetails landlord;

    @Column(name = "is_confirmed", nullable = false)
    private boolean isConfirmed = false;

    @Data
    @Embeddable
    public static class ContactDetails {
        @JsonProperty("name")
        @Column(name = "name", nullable = false)
        private String name;

        @JsonProperty("email")
        @Column(name = "email", nullable = false)
        private String email;

        @JsonProperty("phone")
        @Column(name = "phone", nullable = false)
        private String phone;
    }
}
