package com.forge.talentacquisitionengine.applicationService.application.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class RejectionEmailDraftResponseDto {

    private String subject;
    private String body;
}