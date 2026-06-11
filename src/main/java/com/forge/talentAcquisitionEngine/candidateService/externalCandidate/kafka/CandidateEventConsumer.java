package com.forge.talentacquisitionengine.candidateService.externalCandidate.kafka;

import com.forge.talentacquisitionengine.candidateService.externalCandidate.event.CandidateCreatedEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class CandidateEventConsumer {       //only for test - should be created by team that is consuming

  @KafkaListener(
          topics = "candidate-events",
          groupId = "candidate-group"
  )
  public void consume(CandidateCreatedEvent event) {
    if ("candidate.created".equals(event.getEventType())) {
      System.out.println("Received Event : " + event);
    }
  }
}
