package com.forge.talentacquisitionengine.candidateService.externalCandidate.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CandidateResponse {

    private int status;
    private String message;
    private Boolean duplicate;
}