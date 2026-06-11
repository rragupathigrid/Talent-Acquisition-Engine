package com.forge.talentacquisitionengine.interviewService.scorecard.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.forge.talentacquisitionengine.applicationService.application.entity.Application;
import com.forge.talentacquisitionengine.interviewService.interview.entity.Interview;
import com.forge.talentacquisitionengine.interviewService.scorecard.enums.Recommendation;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(
        name = "scorecard",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "interview_interviewer_unique",
                        columnNames = {"interview_id", "interviewer_id"}
                )
        }
)
public class Scorecard {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "scorecard_id")
    private Long id;

    @NotNull(message = "Interview is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "interview_id", nullable = false)
    private Interview interview;

    @NotNull(message = "Application is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_id", nullable = false)
    private Application application;

    @NotNull(message = "Interviewer ID is required")
    @Positive(message = "Interviewer ID must be positive")
    @Column(name = "interviewer_id", nullable = false)
    private Long interviewerId;

    @NotNull(message = "Score is required")
    @Min(value = 0, message = "Score must be at least 0")
    @Max(value = 100, message = "Score must not exceed 100")
    @Column(name = "score", nullable = false)
    private Integer score;

    @NotNull(message = "Overall score is required")
    @Min(value = 0, message = "Overall score must be at least 0")
    @Max(value = 100, message = "Overall score must not exceed 100")
    @Column(name = "overall_score", nullable = false)
    private Integer overallScore;

    @NotBlank(message = "Competency ratings are required")
    @Column(name = "competency_ratings", nullable = false, columnDefinition = "jsonb")
    private String competencyRatings;

    @NotBlank(message = "Strengths are required")
    @Size(min = 10, max = 2000, message = "Strengths must be between 10 and 2000 characters")
    @Column(name = "strengths", nullable = false, columnDefinition = "TEXT")
    private String strengths;

    @Size(max = 2000, message = "Concerns must not exceed 2000 characters")
    @Column(name = "concerns", columnDefinition = "TEXT")
    private String concerns;

    @Size(max = 3000, message = "Comments must not exceed 3000 characters")
    @Column(name = "comments", columnDefinition = "TEXT")
    private String comments;

    @NotNull(message = "Recommendation is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "recommendation", nullable = false, length = 30)
    private Recommendation recommendation;

    @CreationTimestamp
    @Column(name = "submitted_at", nullable = false, updatable = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime submittedAt;
}