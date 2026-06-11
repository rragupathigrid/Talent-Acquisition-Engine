package com.forge.talentacquisitionengine.interviewService.scorecard.controller;

import com.forge.talentacquisitionengine.interviewService.scorecard.dto.ScorecardRequestDto;
import com.forge.talentacquisitionengine.interviewService.scorecard.dto.ScorecardResponseDto;
import com.forge.talentacquisitionengine.interviewService.scorecard.dto.ScorecardSummaryDto;
import com.forge.talentacquisitionengine.interviewService.scorecard.service.ScorecardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Handles scorecard endpoints as defined in the Forge OpenAPI spec:
 *
 *   POST  /interviews/{interviewId}/scorecards                          – Submit scorecard
 *   GET   /interviews/{interviewId}/scorecards                          – List all scorecards for interview
 *   GET   /interviews/{interviewId}/scorecards/interviewer/{interviewerId} – Single interviewer's scorecard
 *   GET   /demands/{demandId}/scorecards                                – Demand-wide scorecards
 */
@RestController
@RequiredArgsConstructor
public class ScorecardController {

    private final ScorecardService scorecardService;

    /**
     * POST /interviews/{interviewId}/scorecards
     * Submit interview feedback / scorecard.
     */
    @PostMapping("/interviews/{interviewId}/scorecards")
    public ResponseEntity<ScorecardResponseDto> submitScorecard(
            @PathVariable Long interviewId,
            @Valid @RequestBody ScorecardRequestDto requestDto) {

        ScorecardResponseDto response = scorecardService.submitScorecard(interviewId, requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * GET /interviews/{interviewId}/scorecards
     * List all scorecards for a given interview.
     */
    @GetMapping("/interviews/{interviewId}/scorecards")
    public ResponseEntity<List<ScorecardSummaryDto>> getScorecardsByInterview(
            @PathVariable Long interviewId) {

        List<ScorecardSummaryDto> scorecards = scorecardService.getScorecardsByInterview(interviewId);
        return ResponseEntity.ok(scorecards);
    }

    /**
     * GET /interviews/{interviewId}/scorecards/interviewer/{interviewerId}
     * Fetch the scorecard submitted by a specific interviewer for an interview.
     */
    @GetMapping("/interviews/{interviewId}/scorecards/interviewer/{interviewerId}")
    public ResponseEntity<ScorecardResponseDto> getScorecardByInterviewer(
            @PathVariable Long interviewId,
            @PathVariable Long interviewerId) {

        ScorecardResponseDto scorecard = scorecardService.getScorecardByInterviewer(interviewId, interviewerId);
        return ResponseEntity.ok(scorecard);
    }

    /**
     * GET /demands/{demandId}/scorecards
     * Retrieve all scorecards across applications for a demand.
     */
    @GetMapping("/demands/{demandId}/scorecards")
    public ResponseEntity<List<ScorecardSummaryDto>> getScorecardsByDemand(
            @PathVariable String demandId) {

        List<ScorecardSummaryDto> scorecards = scorecardService.getScorecardsByDemand(demandId);
        return ResponseEntity.ok(scorecards);
    }
}