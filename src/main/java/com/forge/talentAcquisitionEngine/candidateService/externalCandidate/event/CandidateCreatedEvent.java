package com.forge.talentacquisitionengine.candidateService.externalCandidate.event;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CandidateCreatedEvent {

  private String eventType;

  private Long candidateId;

  private String firstName;

  private String lastName;

  private String email;

}
