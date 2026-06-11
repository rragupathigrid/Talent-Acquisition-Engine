package com.forge.talentacquisitionengine.offerService.offer.service;

import com.forge.talentacquisitionengine.offerService.offer.entity.Offer;
import com.forge.talentacquisitionengine.offerService.offer.enums.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OfferService {

    /**
     * Create Offer
     */
    Offer createOffer(Offer offer);

    /**
     * Get Offer By ID
     */
    Offer getOfferById(Long id);

    /**
     * Get All Offers
     */
    Page<Offer> getAllOffers(
            Long applicationId,
            Status status,
            Pageable pageable
    );

    /**
     * Update Offer
     */
    Offer updateOffer(
            Long id,
            Offer offer
    );

    /**
     * Send Offer
     */
    Offer sendOffer(Long id);

    /**
     * Accept Offer
     */
    Offer acceptOffer(Long id);

    /**
     * Reject Offer
     */
    Offer rejectOffer(Long id);

    /**
     * Expire Offer
     */
    Offer expireOffer(Long id);

    /**
     * Delete Offer
     */
    void deleteOffer(Long id);
}