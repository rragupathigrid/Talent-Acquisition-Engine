package com.forge.talentacquisitionengine.offerService.offer.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.forge.talentacquisitionengine.offerService.offer.entity.Offer;
import com.forge.talentacquisitionengine.offerService.offer.enums.Status;
import com.forge.talentacquisitionengine.offerService.offer.repository.OfferRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/webhooks/docusign")
@RequiredArgsConstructor
public class DocuSignWebhookController {

    private final OfferRepository offerRepository;

    @PostMapping
    @Transactional
    public ResponseEntity<Void> receive(@RequestBody JsonNode payload) {
        String envelopeId = payload.path("envelopeId").asText(null);
        String status = payload.path("status").asText(null);

        if (envelopeId == null || status == null) {
            return ResponseEntity.badRequest().build();
        }

        offerRepository.findByDocuSignId(envelopeId).ifPresent(offer -> {
            switch (status.toLowerCase()) {
                case "completed" -> {
                    offer.setOfferStatus(Status.SIGNED);
                    offer.setSignedAt(LocalDateTime.now());
                }
                case "declined" -> {
                    offer.setOfferStatus(Status.REJECTED);
                    offer.setRejectedAt(LocalDateTime.now());
                }
                case "voided" -> {
                    offer.setOfferStatus(Status.EXPIRED);
                }
            }
            offerRepository.save(offer);
        });

        return ResponseEntity.ok().build();
    }
}