package com.forge.talentacquisitionengine.interviewService.scorecard.dto;

import com.forge.talentacquisitionengine.interviewService.scorecard.enums.Recommendation;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ScorecardRequestDto {

    @NotNull(message = "Application ID is required")
    private Long applicationId;

    @NotNull(message = "Interviewer ID is required")
    @Positive(message = "Interviewer ID must be positive")
    private Long interviewerId;

    @NotNull(message = "Score is required")
    @Min(value = 0, message = "Score must be at least 0")
    @Max(value = 100, message = "Score must not exceed 100")
    private Integer score;

    @NotNull(message = "Overall score is required")
    @Min(value = 0, message = "Overall score must be at least 0")
    @Max(value = 100, message = "Overall score must not exceed 100")
    private Integer overallScore;

    @NotBlank(message = "Competency ratings are required")
    private String competencyRatings;

    @NotBlank(message = "Strengths are required")
    @Size(min = 10, max = 2000, message = "Strengths must be between 10 and 2000 characters")
    private String strengths;

    @Size(max = 2000, message = "Concerns must not exceed 2000 characters")
    private String concerns;

    @Size(max = 3000, message = "Comments must not exceed 3000 characters")
    private String comments;

    @NotNull(message = "Recommendation is required")
    private Recommendation recommendation;
}