package com.forge.talentacquisitionengine.interviewService.scorecard.service;

import com.forge.talentacquisitionengine.applicationService.application.entity.Application;
import com.forge.talentacquisitionengine.applicationService.application.repository.ApplicationRepository;
import com.forge.talentacquisitionengine.interviewService.interview.entity.Interview;
import com.forge.talentacquisitionengine.interviewService.interview.repository.InterviewRepository;
import com.forge.talentacquisitionengine.interviewService.scorecard.dto.ScorecardRequestDto;
import com.forge.talentacquisitionengine.interviewService.scorecard.dto.ScorecardResponseDto;
import com.forge.talentacquisitionengine.interviewService.scorecard.dto.ScorecardSummaryDto;
import com.forge.talentacquisitionengine.interviewService.scorecard.entity.Scorecard;
import com.forge.talentacquisitionengine.interviewService.scorecard.repository.ScorecardRepository;
import com.forge.talentacquisitionengine.interviewService.interview.enums.Type;
import com.forge.talentacquisitionengine.interviewService.interview.enums.Status;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScorecardService {

    private final ScorecardRepository scorecardRepository;
    private final InterviewRepository interviewRepository;
    private final ApplicationRepository applicationRepository;


    @Transactional
    public ScorecardResponseDto submitScorecard(Long interviewId, ScorecardRequestDto dto) {
        Interview interview = interviewRepository.findById(interviewId)
                .orElseThrow(() -> new EntityNotFoundException("Interview not found with id: " + interviewId));
        if (!interview.getInterviewers()
                .contains(dto.getInterviewerId())) {

            throw new IllegalArgumentException(
                    "Interviewer "
                            + dto.getInterviewerId()
                            + " is not assigned to interview "
                            + interviewId
            );
        }
        if (interview.getStatus() != Status.COMPLETED) {

            throw new IllegalArgumentException(
                    "Scorecards can only be submitted for completed interviews"
            );
        }

        validateCompetencies(
                interview.getInterviewType(),
                dto.getCompetencyRatings()
        );
        Application application = applicationRepository.findById(dto.getApplicationId())
                .orElseThrow(() -> new EntityNotFoundException("Application not found with id: " + dto.getApplicationId()));

        if (scorecardRepository.existsByInterview_IdAndInterviewerId(interviewId, dto.getInterviewerId())) {
            throw new IllegalStateException(
                    "Scorecard already submitted by interviewer " + dto.getInterviewerId()
                            + " for interview " + interviewId);
        }

        Scorecard scorecard = new Scorecard();
        scorecard.setInterview(interview);
        scorecard.setApplication(application);
        scorecard.setInterviewerId(dto.getInterviewerId());
        scorecard.setScore(dto.getScore());
        scorecard.setOverallScore(dto.getOverallScore());
        scorecard.setCompetencyRatings(dto.getCompetencyRatings());
        scorecard.setStrengths(dto.getStrengths());
        scorecard.setConcerns(dto.getConcerns());
        scorecard.setComments(dto.getComments());
        scorecard.setRecommendation(dto.getRecommendation());

        Scorecard saved = scorecardRepository.save(scorecard);
        return toResponseDto(saved);
    }

    @Transactional(readOnly = true)
    public List<ScorecardSummaryDto> getScorecardsByInterview(Long interviewId) {
        if (!interviewRepository.existsById(interviewId)) {
            throw new EntityNotFoundException("Interview not found with id: " + interviewId);
        }
        return scorecardRepository.findAllByInterview_Id(interviewId)
                .stream()
                .map(this::toSummaryDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ScorecardResponseDto getScorecardByInterviewer(Long interviewId, Long interviewerId) {
        if (!interviewRepository.existsById(interviewId)) {
            throw new EntityNotFoundException("Interview not found with id: " + interviewId);
        }
        Scorecard scorecard = scorecardRepository.findByInterview_IdAndInterviewerId(interviewId, interviewerId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Scorecard not found for interviewer " + interviewerId
                                + " in interview " + interviewId));
        return toResponseDto(scorecard);
    }

    @Transactional(readOnly = true)
    public List<ScorecardSummaryDto> getScorecardsByDemand(String demandId) {
        return scorecardRepository.findAll()
                .stream()
//                .filter(sc -> demandId.equals(sc.getApplication().getDemand().getDemandId()))
                .map(this::toSummaryDto)
                .collect(Collectors.toList());
    }

    // ── Mappers ──────────────────────────────────────────────────────────────

    private ScorecardResponseDto toResponseDto(Scorecard sc) {
        return ScorecardResponseDto.builder()
                .scorecardId(sc.getId())
                .interviewId(sc.getInterview().getId())
                .applicationId(sc.getApplication().getId())
                .interviewerId(sc.getInterviewerId())
                .score(sc.getScore())
                .overallScore(sc.getOverallScore())
                .competencyRatings(sc.getCompetencyRatings())
                .strengths(sc.getStrengths())
                .concerns(sc.getConcerns())
                .comments(sc.getComments())
                .recommendation(sc.getRecommendation())
                .submittedAt(sc.getSubmittedAt())
                .build();
    }

    private ScorecardSummaryDto toSummaryDto(Scorecard sc) {
        return ScorecardSummaryDto.builder()
                .scorecardId(sc.getId())
                .interviewerId(sc.getInterviewerId())
                .score(sc.getScore())
                .recommendation(sc.getRecommendation())
                .build();
    }

    private void validateCompetencies(
            Type interviewType,
            String competencyRatings
    ) {

        competencyRatings = competencyRatings.toLowerCase();

        switch (interviewType) {

            case TECHNICAL -> {
                if (!competencyRatings.contains("coding")
                        || !competencyRatings.contains("dsa")
                        || !competencyRatings.contains("systemdesign")) {
                    throw new IllegalArgumentException(
                            "Technical interview requires coding, dsa and system design ratings"
                    );
                }
            }

            case BEHAVIOURAL -> {
                if (!competencyRatings.contains("communication")
                        || !competencyRatings.contains("leadership")
                        || !competencyRatings.contains("collaboration")) {
                    throw new IllegalArgumentException(
                            "Behavioural interview requires communication, leadership and collaboration ratings"
                    );
                }
            }

            case CULTURAL_FIT -> {
                if (!competencyRatings.contains("culturefit")
                        || !competencyRatings.contains("ownership")
                        || !competencyRatings.contains("teamwork")) {
                    throw new IllegalArgumentException(
                            "Cultural fit interview requires cultureFit, ownership and teamwork ratings"
                    );
                }
            }

            case FINAL -> {
                if (!competencyRatings.contains("overallassessment")) {
                    throw new IllegalArgumentException(
                            "Final round requires overallAssessment rating"
                    );
                }
            }
        }
    }
}