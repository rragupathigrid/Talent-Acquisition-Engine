package com.forge.talentacquisitionengine.interviewService.interview.controller;

import com.forge.talentacquisitionengine.interviewService.interview.entity.Interview;
import com.forge.talentacquisitionengine.interviewService.interview.enums.Status;
import com.forge.talentacquisitionengine.interviewService.interview.service.InterviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/interviews")
@RequiredArgsConstructor
public class InterviewController {

    private final InterviewService interviewService;

    /**
     * Create Interview
     */
    @PostMapping
    public ResponseEntity<Interview> createInterview(
            @Valid @RequestBody Interview interview
    ) {

        Interview createdInterview =
                interviewService.createInterview(interview);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createdInterview);
    }

    /**
     * Get Interview By ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Interview> getInterviewById(
            @PathVariable Long id
    ) {

        Interview interview =
                interviewService.getInterviewById(id);

        return ResponseEntity.ok(interview);
    }

    /**
     * Get All Interviews
     */
    @GetMapping
    public ResponseEntity<Page<Interview>> getAllInterviews(
            @RequestParam(required = false) Long applicationId,
            @RequestParam(required = false) Status status,
            Pageable pageable
    ) {

        Page<Interview> interviews =
                interviewService.getAllInterviews(
                        applicationId,
                        status,
                        pageable
                );

        return ResponseEntity.ok(interviews);
    }

    /**
     * Update Interview
     */
    @PutMapping("/{id}")
    public ResponseEntity<Interview> updateInterview(
            @PathVariable Long id,
            @Valid @RequestBody Interview interview
    ) {

        Interview updatedInterview =
                interviewService.updateInterview(id, interview);

        return ResponseEntity.ok(updatedInterview);
    }

    /**
     * Cancel Interview
     */
    @PatchMapping("/{id}/cancel")
    public ResponseEntity<Interview> cancelInterview(
            @PathVariable Long id
    ) {

        Interview cancelledInterview =
                interviewService.cancelInterview(id);

        return ResponseEntity.ok(cancelledInterview);
    }

    /**
     * Mark Interview Completed
     */
    @PatchMapping("/{id}/complete")
    public ResponseEntity<Interview> completeInterview(
            @PathVariable Long id
    ) {

        Interview completedInterview =
                interviewService.completeInterview(id);

        return ResponseEntity.ok(completedInterview);
    }

    /**
     * Delete Interview
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInterview(
            @PathVariable Long id
    ) {

        interviewService.deleteInterview(id);

        return ResponseEntity.noContent().build();
    }
}