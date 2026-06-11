package com.forge.talentacquisitionengine.candidateService.externalCandidate.kafka;

import lombok.RequiredArgsConstructor;
import com.forge.talentacquisitionengine.candidateService.externalCandidate.event.CandidateCreatedEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CandidateEventProducer {

  private final KafkaTemplate<String, CandidateCreatedEvent> kafkaTemplate;

  private static final String TOPIC_NAME = "candidate-events";

  public void publishCandidateCreatedEvent(
          CandidateCreatedEvent event
  ) {
    kafkaTemplate.send(
            TOPIC_NAME,
            String.valueOf(event.getCandidateId()),
            event
    );
  }
}
