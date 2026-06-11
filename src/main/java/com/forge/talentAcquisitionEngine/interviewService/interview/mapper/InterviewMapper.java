package com.forge.talentacquisitionengine.interviewService.interview.mapper;

import com.forge.talentacquisitionengine.applicationService.application.entity.Application;
import com.forge.talentacquisitionengine.interviewService.interview.dto.InterviewDto;
import com.forge.talentacquisitionengine.interviewService.interview.entity.Interview;
import com.forge.talentacquisitionengine.interviewService.scorecard.entity.Scorecard;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class InterviewMapper {

  private InterviewMapper() {
  }

  public static Interview dtoToEntity(
      InterviewDto dto,
      Application application
  ) {

    if (dto == null) {
      return null;
    }

    Interview entity = new Interview();

    entity.setId(
        dto.getId()
    );

    entity.setApplication(
        application
    );

    entity.setInterviewers(
        dto.getInterviewers()
    );

    entity.setInterviewType(
        dto.getInterviewType()
    );

    entity.setScheduledAt(
        dto.getScheduledAt()
    );

    entity.setDurationMins(
        dto.getDurationMins()
    );

    entity.setTimeZone(
        dto.getTimeZone()
    );

    entity.setMeetLink(
        dto.getMeetLink()
    );

    entity.setStatus(
        dto.getStatus()
    );

    entity.setCalendarEventId(
        dto.getCalendarEventId()
    );

    entity.setCreatedAt(
        dto.getCreatedAt()
    );

    entity.setUpdatedAt(
        dto.getUpdatedAt()
    );

    return entity;
  }


  public static InterviewDto entityToDto(
      Interview entity
  ) {

    if (entity == null) {
      return null;
    }

    InterviewDto dto = new InterviewDto();

    dto.setId(
        entity.getId()
    );

    if (entity.getApplication() != null) {

      dto.setApplicationId(
          entity.getApplication().getId()
      );
    }

    dto.setInterviewers(
        entity.getInterviewers()
    );

    dto.setInterviewType(
        entity.getInterviewType()
    );

    dto.setScheduledAt(
        entity.getScheduledAt()
    );

    dto.setDurationMins(
        entity.getDurationMins()
    );

    dto.setTimeZone(
        entity.getTimeZone()
    );

    dto.setMeetLink(
        entity.getMeetLink()
    );

    dto.setStatus(
        entity.getStatus()
    );

    dto.setCalendarEventId(
        entity.getCalendarEventId()
    );

    dto.setCreatedAt(
        entity.getCreatedAt()
    );

    dto.setUpdatedAt(
        entity.getUpdatedAt()
    );

    if (entity.getScorecards() != null) {

      List<Long> scorecardIds =
          entity.getScorecards()
              .stream()
              .map(Scorecard::getId)
              .collect(Collectors.toList());

      dto.setScorecardIds(
          scorecardIds
      );

    } else {

      dto.setScorecardIds(
          new ArrayList<>()
      );
    }

    return dto;
  }
}