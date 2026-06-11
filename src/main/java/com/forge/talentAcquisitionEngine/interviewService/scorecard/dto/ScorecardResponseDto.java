package com.forge.talentacquisitionengine.interviewService.scorecard.dto;

import com.forge.talentacquisitionengine.interviewService.scorecard.enums.Recommendation;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class ScorecardResponseDto {

    private Long scorecardId;
    private Long interviewId;
    private Long applicationId;
    private Long interviewerId;
    private Integer score;
    private Integer overallScore;
    private String competencyRatings;
    private String strengths;
    private String concerns;
    private String comments;
    private Recommendation recommendation;
    private LocalDateTime submittedAt;
}