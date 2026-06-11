package com.forge.talentacquisitionengine.offerService.offer.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.forge.talentacquisitionengine.applicationService.application.entity.Application;
import com.forge.talentacquisitionengine.offerService.offer.enums.Status;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "offer")
public class Offer {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "offer_id")
    private Long id;

    @NotNull(message = "Application is required")
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "application_id", nullable = false)
    private Application application;

    @NotBlank(message = "Role is required")
    @Size(max = 100, message = "Role must not exceed 100 characters")
    @Column(name = "role", nullable = false)
    private String role;

    @NotNull(message = "Base salary is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Base salary must be greater than 0")
    @Column(name = "base_salary", nullable = false)
    private BigDecimal baseSalary;

    @DecimalMin(value = "0.0", message = "Bonus cannot be negative")
    @Column(name = "bonus")
    private BigDecimal bonus;

    @DecimalMin(value = "0.0", message = "Equity cannot be negative")
    @Column(name = "equity")
    private BigDecimal equity;

    @NotNull(message = "Joining date is required")
    @Future(message = "Joining date must be in the future")
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Column(name = "joining_date", nullable = false)
    private LocalDate joiningDate;

    @NotNull(message = "Offer status is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "offer_status", nullable = false)
    private Status offerStatus;

    @NotBlank(message = "Employment type is required")
    @Size(max = 50, message = "Employment type must not exceed 50 characters")
    @Column(name = "employment_type", nullable = false)
    private String employmentType;

//    @Size(max = 100, message = "Template ID must not exceed 100 characters")
//    @Column(name = "template_id", length = 100)
//    private String templateId;

//    @NotBlank(message = "Offer template content is required")
//    @Size(min = 50, max = 10000, message = "Offer template content must be between 50 and 10000 characters")
//    @Column(name = "offer_template_content", nullable = false, columnDefinition = "TEXT")
//    private String offerTemplateContent;

//    @NotBlank(message = "Approval chain is required")
//    @Column(name = "approval_chain", nullable = false, columnDefinition = "jsonb")
//    private String approvalChain;

    @Size(max = 100, message = "Approved by must not exceed 100 characters")
    @Column(name = "approved_by")
    private String approvedBy;

    @Size(max = 100, message = "Rejected by must not exceed 100 characters")
    @Column(name = "rejected_by")
    private String rejectedBy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "rejected_at")
    private LocalDateTime rejectedAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "sent_at")
    private LocalDateTime sentAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "signed_at")
    private LocalDateTime signedAt;

    @Future(message = "Offer expiry date must be in the future")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    @Size(max = 150, message = "DocuSign ID must not exceed 150 characters")
    @Column(name = "docu_sign_id", length = 150)
    private String docuSignId;

    @PrePersist
    private void onCreate() {
        if (this.offerStatus == null) {
            this.offerStatus = Status.DRAFT;
        }
    }
}