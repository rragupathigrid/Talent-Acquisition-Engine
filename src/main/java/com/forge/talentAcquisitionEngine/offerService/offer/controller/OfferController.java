package com.forge.talentacquisitionengine.offerService.offer.controller;

import com.forge.talentacquisitionengine.offerService.offer.entity.Offer;
import com.forge.talentacquisitionengine.offerService.offer.enums.Status;
import com.forge.talentacquisitionengine.offerService.offer.service.OfferService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/offers")
@RequiredArgsConstructor
public class OfferController {

    private final OfferService offerService;

    /**
     * Create Offer
     */
    @PostMapping
    public ResponseEntity<Offer> createOffer(
            @Valid @RequestBody Offer offer
    ) {

        Offer createdOffer =
                offerService.createOffer(offer);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createdOffer);
    }

    /**
     * Get Offer By ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Offer> getOfferById(
            @PathVariable Long id
    ) {

        Offer offer =
                offerService.getOfferById(id);

        return ResponseEntity.ok(offer);
    }

    /**
     * Get All Offers
     */
    @GetMapping
    public ResponseEntity<Page<Offer>> getAllOffers(
            @RequestParam(required = false)
            Long applicationId,

            @RequestParam(required = false)
            Status status,

            Pageable pageable
    ) {

        Page<Offer> offers =
                offerService.getAllOffers(
                        applicationId,
                        status,
                        pageable
                );

        return ResponseEntity.ok(offers);
    }

    /**
     * Update Offer
     */
    @PutMapping("/{id}")
    public ResponseEntity<Offer> updateOffer(
            @PathVariable Long id,
            @Valid @RequestBody Offer offer
    ) {

        Offer updatedOffer =
                offerService.updateOffer(id, offer);

        return ResponseEntity.ok(updatedOffer);
    }

    /**
     * Send Offer
     */
    @PatchMapping("/{id}/send")
    public ResponseEntity<Offer> sendOffer(
            @PathVariable Long id
    ) {

        Offer sentOffer =
                offerService.sendOffer(id);

        return ResponseEntity.ok(sentOffer);
    }

    /**
     * Accept Offer
     */
    @PatchMapping("/{id}/accept")
    public ResponseEntity<Offer> acceptOffer(
            @PathVariable Long id
    ) {

        Offer acceptedOffer =
                offerService.acceptOffer(id);

        return ResponseEntity.ok(acceptedOffer);
    }

    /**
     * Reject Offer
     */
    @PatchMapping("/{id}/reject")
    public ResponseEntity<Offer> rejectOffer(
            @PathVariable Long id
    ) {

        Offer rejectedOffer =
                offerService.rejectOffer(id);

        return ResponseEntity.ok(rejectedOffer);
    }


    /**
     * Expire Offer
     */
    @PatchMapping("/{id}/expire")
    public ResponseEntity<Offer> expireOffer(
            @PathVariable Long id
    ) {

        Offer expiredOffer =
                offerService.expireOffer(id);

        return ResponseEntity.ok(expiredOffer);
    }

    /**
     * Delete Offer
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOffer(
            @PathVariable Long id
    ) {

        offerService.deleteOffer(id);

        return ResponseEntity.noContent().build();
    }
}