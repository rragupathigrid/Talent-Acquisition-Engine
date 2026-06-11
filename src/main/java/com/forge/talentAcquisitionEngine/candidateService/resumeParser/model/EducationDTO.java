package com.forge.talentacquisitionengine.candidateService.resumeParser.model;


public record EducationDTO(
        String institutionName,
        String degree,
        String major,
        String startDate,
        String endDate,
        String gpa
) {}
