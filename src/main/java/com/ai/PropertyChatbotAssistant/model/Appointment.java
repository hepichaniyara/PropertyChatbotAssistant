package com.ai.PropertyChatbotAssistant.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Data;

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
    private TenantContactDetails tenant;

    @JsonProperty("landlord")
    @Embedded
    private LandlordContactDetails landlord;

    @Column(name = "is_confirmed", nullable = false)
    private boolean isConfirmed = false;

    @Data
    @Embeddable
    public static class TenantContactDetails {
        @JsonProperty("name")
        @Column(name = "tenant_name", nullable = false)
        private String tenantName;

        @JsonProperty("email")
        @Column(name = "tenant_email", nullable = false)
        private String tenantEmail;

        @JsonProperty("phone")
        @Column(name = "tenant_phone", nullable = false)
        private String tenantPhone;
    }

    @Data
    @Embeddable
    public static class LandlordContactDetails {
        @JsonProperty("name")
        @Column(name = "landlord_name", nullable = false)
        private String landlordName;

        @JsonProperty("email")
        @Column(name = "landlord_email", nullable = false)
        private String landlordEmail;

        @JsonProperty("phone")
        @Column(name = "landlord_phone", nullable = false)
        private String landlordPhone;
    }
}
