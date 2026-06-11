package com.forge.talentacquisitionengine.interviewService.interview.service;

import com.forge.talentacquisitionengine.applicationService.application.entity.Application;
import com.forge.talentacquisitionengine.applicationService.application.repository.ApplicationRepository;
import com.forge.talentacquisitionengine.interviewService.interview.entity.Interview;
import com.forge.talentacquisitionengine.interviewService.interview.enums.Status;
import com.forge.talentacquisitionengine.interviewService.interview.integration.GoogleCalendarClient;
import com.forge.talentacquisitionengine.interviewService.interview.repository.InterviewRepository;
import com.forge.talentacquisitionengine.interviewService.interview.integration.GoogleCalendarResponse;
import jakarta.persistence.EntityNotFoundException;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InterviewServiceImpl implements InterviewService {

    private final InterviewRepository interviewRepository;
    private final ApplicationRepository applicationRepository;
    private final GoogleCalendarClient googleCalendarClient;
    /**
     * Create Interview
     */
    @Override
    public Interview createInterview(Interview interview) {

        /*
         * Validate Application Exists
         */
        Long applicationId =
                interview.getApplication().getId();

        Application application =
                applicationRepository.findById(applicationId)
                        .orElseThrow(() ->
                                new EntityNotFoundException(
                                        "Application not found with ID: "
                                                + applicationId
                                )
                        );

        interview.setApplication(application);

        /*
         * Default Status
         */
        if (interview.getStatus() == null) {
            interview.setStatus(Status.SCHEDULED);
        }

        /*
         * Save Interview
         */
        GoogleCalendarResponse response =
                googleCalendarClient.createEvent(
                        interview
                );

        interview.setCalendarEventId(
                response.getEventId()
        );

        interview.setMeetLink(
                response.getMeetLink()
        );

        return interviewRepository.save(
                interview
        );
    }

    /**
     * Get Interview By ID
     */
    @Override
    public Interview getInterviewById(Long id) {

        return interviewRepository.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "Interview not found with ID: " + id
                        )
                );
    }

    /**
     * Get All Interviews
     */
    @Override
    public Page<Interview> getAllInterviews(
            Long applicationId,
            Status status,
            Pageable pageable
    ) {

        /*
         * Filter By Application + Status
         */
        if (applicationId != null && status != null) {

            return interviewRepository
                    .findByApplicationIdAndStatus(
                            applicationId,
                            status,
                            pageable
                    );
        }

        /*
         * Filter By Application
         */
        if (applicationId != null) {

            return interviewRepository
                    .findByApplicationId(
                            applicationId,
                            pageable
                    );
        }

        /*
         * Filter By Status
         */
        if (status != null) {

            return interviewRepository
                    .findByStatus(
                            status,
                            pageable
                    );
        }

        /*
         * Fetch All
         */
        return interviewRepository.findAll(pageable);
    }

    /**
     * Update Interview
     */
    @Override
    public Interview updateInterview(
            Long id,
            Interview updatedInterview
    ) {

        Interview existingInterview =
                getInterviewById(id);

        /*
         * Update Fields
         */
        existingInterview.setInterviewType(
                updatedInterview.getInterviewType()
        );

        existingInterview.setInterviewers(
                updatedInterview.getInterviewers()
        );

        existingInterview.setScheduledAt(
                updatedInterview.getScheduledAt()
        );

        existingInterview.setDurationMins(
                updatedInterview.getDurationMins()
        );

        existingInterview.setTimeZone(
                updatedInterview.getTimeZone()
        );

        existingInterview.setMeetLink(
                updatedInterview.getMeetLink()
        );

        existingInterview.setCalendarEventId(
                updatedInterview.getCalendarEventId()
        );

        /*
         * Save Updated Interview
         */
        if(existingInterview.getCalendarEventId() != null) {

            googleCalendarClient.updateEvent(
                    existingInterview.getCalendarEventId(),
                    existingInterview
            );
        }
        return interviewRepository.save(existingInterview);
    }

    /**
     * Cancel Interview
     */
    @Override
    public Interview cancelInterview(Long id) {

        Interview interview =
                getInterviewById(id);

        /*
         * Business Validation
         */
        if (interview.getStatus() == Status.COMPLETED) {

            throw new IllegalStateException(
                    "Completed interview cannot be cancelled"
            );
        }

        interview.setStatus(Status.CANCELLED);
        if(interview.getCalendarEventId() != null) {

            googleCalendarClient.deleteEvent(
                    interview.getCalendarEventId()
            );
        }

        return interviewRepository.save(interview);
    }

    /**
     * Complete Interview
     */
    @Override
    public Interview completeInterview(Long id) {

        Interview interview =
                getInterviewById(id);

        /*
         * Business Validation
         */
        if (interview.getStatus() == Status.CANCELLED) {

            throw new IllegalStateException(
                    "Cancelled interview cannot be completed"
            );
        }

        interview.setStatus(Status.COMPLETED);

        return interviewRepository.save(interview);
    }

    /**
     * Delete Interview
     */
    @Override
    public void deleteInterview(Long id) {

        Interview interview =
                getInterviewById(id);

        interviewRepository.delete(interview);
    }
}