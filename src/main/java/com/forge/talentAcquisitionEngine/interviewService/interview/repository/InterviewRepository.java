package com.forge.talentacquisitionengine.interviewService.interview.repository;

import com.forge.talentacquisitionengine.interviewService.interview.entity.Interview;
import com.forge.talentacquisitionengine.interviewService.interview.enums.Status;
import com.forge.talentacquisitionengine.interviewService.interview.enums.Type;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface InterviewRepository
        extends JpaRepository<Interview, Long> {

    /**
     * Find all interviews for an application
     */
    Page<Interview> findByApplicationId(
            Long applicationId,
            Pageable pageable
    );

    /**
     * Find all interviews by status
     */
    Page<Interview> findByStatus(
            Status status,
            Pageable pageable
    );

    /**
     * Find interviews by application + status
     */
    Page<Interview> findByApplicationIdAndStatus(
            Long applicationId,
            Status status,
            Pageable pageable
    );

    /**
     * Find interviews by type
     */
    Page<Interview> findByInterviewType(
            Type interviewType,
            Pageable pageable
    );

    /**
     * Find interviews scheduled between dates
     */
    List<Interview> findByScheduledAtBetween(
            LocalDateTime start,
            LocalDateTime end
    );

    /**
     * Find upcoming interviews
     */
    List<Interview> findByScheduledAtAfter(
            LocalDateTime dateTime
    );

    /**
     * Find interviews for a specific interviewer
     */
    List<Interview> findByInterviewersContains(
            Long interviewerEmployeeId
    );

    /**
     * Check overlapping interviews
     */
    List<Interview> findByScheduledAtBetweenAndInterviewersContains(
            LocalDateTime start,
            LocalDateTime end,
            Long interviewerEmployeeId
    );

    /**
     * Count interviews by status
     */
    long countByStatus(Status status);

    /**
     * Count interviews for application
     */
    long countByApplicationId(Long applicationId);

    /**
     * Exists by calendar event ID
     */
    boolean existsByCalendarEventId(
            String calendarEventId
    );
}