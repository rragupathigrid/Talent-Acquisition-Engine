package com.forge.talentacquisitionengine.interviewService.interview.service;

import com.forge.talentacquisitionengine.interviewService.interview.entity.Interview;
import com.forge.talentacquisitionengine.interviewService.interview.enums.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface InterviewService {

    Interview createInterview(Interview interview);

    Interview getInterviewById(Long id);

    Page<Interview> getAllInterviews(
            Long applicationId,
            Status status,
            Pageable pageable
    );

    Interview updateInterview(
            Long id,
            Interview interview
    );

    Interview cancelInterview(Long id);

    Interview completeInterview(Long id);

    void deleteInterview(Long id);
}