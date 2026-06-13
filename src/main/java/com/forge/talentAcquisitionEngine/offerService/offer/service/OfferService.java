package com.forge.talentacquisitionengine.offerService.offer.service;

import com.forge.talentacquisitionengine.offerService.offer.dto.DocuSignWebhookDto;
import com.forge.talentacquisitionengine.offerService.offer.entity.Offer;
import com.forge.talentacquisitionengine.offerService.offer.enums.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.forge.talentacquisitionengine.offerService.offer.dto.ApprovalChainRequestDto;
import com.forge.talentacquisitionengine.offerService.offer.dto.ApprovalStepDto;
import java.util.List;

public interface OfferService {

    Offer saveApprovalChain(Long offerId, ApprovalChainRequestDto request);

    Offer submitForApproval(Long offerId);

    Offer approveCurrentStep(Long offerId, String approverEmail);

    Offer rejectApproval(Long offerId, String approverEmail, String comments);

    List<ApprovalStepDto> getApprovalChain(Long offerId);
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

    Offer approveOffer(
            Long offerId,
            String approverEmail
    );

    void handleDocuSignWebhook(
            DocuSignWebhookDto dto
    );
}