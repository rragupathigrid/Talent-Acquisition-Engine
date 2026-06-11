package com.forge.talentacquisitionengine.interviewService.scorecard.dto;

import com.forge.talentacquisitionengine.interviewService.scorecard.enums.Recommendation;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ScorecardSummaryDto {

    private Long scorecardId;
    private Long interviewerId;
    private String interviewerName;   // resolved from employee service if needed
    private Integer score;
    private Recommendation recommendation;
}