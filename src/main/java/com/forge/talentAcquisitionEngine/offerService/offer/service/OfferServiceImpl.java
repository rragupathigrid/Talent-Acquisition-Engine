package com.forge.talentacquisitionengine.offerService.offer.service;

import com.forge.talentacquisitionengine.applicationService.application.entity.Application;
import com.forge.talentacquisitionengine.applicationService.application.repository.ApplicationRepository;
import com.forge.talentacquisitionengine.offerService.offer.dto.ApprovalChainRequestDto;
import com.forge.talentacquisitionengine.offerService.offer.dto.ApprovalStep;
import com.forge.talentacquisitionengine.offerService.offer.dto.ApprovalStepDto;
import com.forge.talentacquisitionengine.offerService.offer.dto.DocuSignWebhookDto;
import com.forge.talentacquisitionengine.offerService.offer.entity.Offer;
import com.forge.talentacquisitionengine.offerService.offer.enums.Status;
import com.forge.talentacquisitionengine.offerService.offer.integration.DocuSignClient;
import com.forge.talentacquisitionengine.offerService.offer.repository.OfferRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OfferServiceImpl implements OfferService {

    private final OfferRepository offerRepository;
    private final ApplicationRepository applicationRepository;
    private final DocuSignClient docuSignClient;

    /**
     * Create Offer
     */
    @Override
    public Offer createOffer(Offer offer) {

        /*
         * Validate Application Exists
         */
        Long applicationId =
                offer.getApplication().getId();

        Application application =
                applicationRepository.findById(applicationId)
                        .orElseThrow(() ->
                                new EntityNotFoundException(
                                        "Application not found with ID: "
                                                + applicationId
                                )
                        );

        offer.setApplication(application);

        /*
         * Default Status
         */
        if (offer.getOfferStatus() == null) {
            offer.setOfferStatus(Status.DRAFT);
        }

        /*
         * Save Offer
         */
        return offerRepository.save(offer);
    }

    /**
     * Get Offer By ID
     */
    @Override
    public Offer getOfferById(Long id) {

        return offerRepository.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "Offer not found with ID: " + id
                        )
                );
    }

    /**
     * Get All Offers
     */
    @Override
    public Page<Offer> getAllOffers(
            Long applicationId,
            Status status,
            Pageable pageable
    ) {

        /*
         * Filter By Application + Status
         */
        if (applicationId != null && status != null) {

            return offerRepository
                    .findByApplicationIdAndOfferStatus(
                            applicationId,
                            status,
                            pageable
                    );
        }

        /*
         * Filter By Application
         */
        if (applicationId != null) {

            return offerRepository
                    .findByApplicationId(
                            applicationId,
                            pageable
                    );
        }

        /*
         * Filter By Status
         */
        if (status != null) {

            return offerRepository
                    .findByOfferStatus(
                            status,
                            pageable
                    );
        }

        /*
         * Fetch All
         */
        return offerRepository.findAll(pageable);
    }

    /**
     * Update Offer
     */
    @Override
    public Offer updateOffer(
            Long id,
            Offer updatedOffer
    ) {

        Offer existingOffer =
                getOfferById(id);

        /*
         * Update Allowed Fields
         */
        existingOffer.setBaseSalary(
                updatedOffer.getBaseSalary()
        );

        existingOffer.setBonus(
                updatedOffer.getBonus()
        );

        existingOffer.setJoiningDate(
                updatedOffer.getJoiningDate()
        );

        /*
         * Save Updated Offer
         */
        return offerRepository.save(existingOffer);
    }

    /**
     * Send Offer (via DocuSign — must be APPROVED first)
     */
    @Override
    public Offer sendOffer(Long id) {
        Offer offer = getOfferById(id);

        if (offer.getOfferStatus() != Status.APPROVED) {
            throw new IllegalStateException("Only approved offers can be sent");
        }

        // Send to DocuSign — this emails the candidate directly
        String envelopeId = docuSignClient.createEnvelope(offer);

        offer.setDocuSignId(envelopeId);
        offer.setOfferStatus(Status.SENT);
        offer.setSentAt(LocalDateTime.now());

        return offerRepository.save(offer);
    }

    /**
     * Accept Offer (candidate accepts — status moves to SIGNED)
     */
    @Override
    public Offer acceptOffer(Long id) {

        Offer offer =
                getOfferById(id);

        /*
         * Business Validation
         */
        if (offer.getOfferStatus() != Status.SENT) {

            throw new IllegalStateException(
                    "Only sent offers can be accepted"
            );
        }

        offer.setOfferStatus(Status.SIGNED);

        return offerRepository.save(offer);
    }

    /**
     * Reject Offer
     */
    @Override
    public Offer rejectOffer(Long id) {

        Offer offer =
                getOfferById(id);

        /*
         * Business Validation
         */
        if (offer.getOfferStatus() != Status.SENT) {

            throw new IllegalStateException(
                    "Only sent offers can be rejected"
            );
        }

        offer.setOfferStatus(Status.REJECTED);

        return offerRepository.save(offer);
    }

    /**
     * Expire Offer
     */
    @Override
    public Offer expireOffer(Long id) {

        Offer offer =
                getOfferById(id);

        /*
         * Prevent Expiring Accepted/Signed Offer
         */
        if (offer.getOfferStatus() == Status.SIGNED) {

            throw new IllegalStateException(
                    "Signed offer cannot expire"
            );
        }

        offer.setOfferStatus(Status.EXPIRED);

        return offerRepository.save(offer);
    }

    /**
     * Delete Offer
     */
    @Override
    public void deleteOffer(Long id) {

        Offer offer =
                getOfferById(id);

        offerRepository.delete(offer);
    }

    /**
     * Save Approval Chain
     */
    @Override
    public Offer saveApprovalChain(Long offerId, ApprovalChainRequestDto request) {
        Offer offer = getOfferById(offerId);

        List<ApprovalStepDto> steps = new ArrayList<>(request.getApprovalSteps());
        steps.sort(Comparator.comparing(ApprovalStepDto::getStepOrder));

        if (steps.isEmpty()) {
            throw new IllegalArgumentException("Approval chain is required");
        }

        for (int i = 0; i < steps.size(); i++) {
            ApprovalStepDto step = steps.get(i);
            if (step.getStepOrder() == null || step.getStepOrder() != i + 1) {
                throw new IllegalArgumentException("Approval steps must be ordered sequentially from 1");
            }
            if (step.getApproverEmail() == null || step.getApproverEmail().isBlank()) {
                throw new IllegalArgumentException("Approver email is required for every step");
            }
            if (step.getApproved() == null) {
                step.setApproved(false);
            }
        }

        // Convert ApprovalStepDto list to ApprovalStep list for the entity
        List<ApprovalStep> chain = steps.stream()
                .map(dto -> {
                    ApprovalStep step = new ApprovalStep();
                    step.setOrderNumber(dto.getStepOrder());
                    step.setApproverEmail(dto.getApproverEmail());
                    step.setApproved(dto.getApproved());
                    step.setApprovedBy(dto.getApprovedBy());
                    return step;
                }).toList();

        offer.setApprovalChain(chain);
        offer.setCurrentApprovalStep(0);
        return offerRepository.save(offer);
    }

    /**
     * Submit For Approval
     */
    @Override
    public Offer submitForApproval(Long offerId) {
        Offer offer = getOfferById(offerId);

        if (offer.getApprovalChain() == null || offer.getApprovalChain().isEmpty()) {
            throw new IllegalStateException("Approval chain is required before submitting for approval");
        }

        if (offer.getOfferStatus() != Status.DRAFT) {
            throw new IllegalStateException("Only draft offers can be submitted for approval");
        }

        offer.setOfferStatus(Status.PENDING_APPROVAL);
        offer.setCurrentApprovalStep(1);
        return offerRepository.save(offer);
    }

    /**
     * Approve Current Step
     */
    @Override
    public Offer approveCurrentStep(Long offerId, String approverEmail) {
        Offer offer = getOfferById(offerId);
        List<ApprovalStep> steps = offer.getApprovalChain();

        if (steps == null || steps.isEmpty()) {
            throw new IllegalStateException("Approval chain not configured");
        }

        Integer currentStepNumber = offer.getCurrentApprovalStep();
        if (currentStepNumber == null || currentStepNumber < 1) {
            throw new IllegalStateException("Offer is not currently in approval flow");
        }

        int currentIndex = currentStepNumber - 1;
        if (currentIndex >= steps.size()) {
            throw new IllegalStateException("Offer already fully approved");
        }

        ApprovalStep currentStep = steps.get(currentIndex);

        if (!currentStep.getApproverEmail().equalsIgnoreCase(approverEmail)) {
            throw new IllegalStateException("Only the current approver can approve this step");
        }

        currentStep.setApproved(true);
        currentStep.setApprovedBy(approverEmail);

        boolean allApproved = steps.stream().allMatch(step -> Boolean.TRUE.equals(step.getApproved()));

        if (allApproved) {
            offer.setOfferStatus(Status.APPROVED);
            offer.setApprovedBy(approverEmail);
            offer.setApprovedAt(LocalDateTime.now());
            offer.setCurrentApprovalStep(steps.size());
        } else {
            // Still in approval flow — advance to next step
            offer.setOfferStatus(Status.PENDING_APPROVAL);
            offer.setCurrentApprovalStep(currentStepNumber + 1);
        }

        return offerRepository.save(offer);
    }

    /**
     * Approve Offer (legacy method — direct approval without step tracking)
     */
    @Override
    public Offer approveOffer(Long offerId, String approverEmail) {

        Offer offer = getOfferById(offerId);

        List<ApprovalStep> chain = offer.getApprovalChain();

        ApprovalStep currentStep = null;

        if (chain != null) {
            for (ApprovalStep step : chain) {
                if (!Boolean.TRUE.equals(step.getApproved())) {
                    currentStep = step;
                    break;
                }
            }
        }

        if (currentStep == null) {
            throw new IllegalStateException("Offer already fully approved");
        }

        if (!currentStep.getApproverEmail().equalsIgnoreCase(approverEmail)) {
            throw new IllegalStateException("Not current approver");
        }

        currentStep.setApproved(true);
        currentStep.setApprovedBy(approverEmail);

        boolean allApproved =
                chain.stream().allMatch(ApprovalStep::getApproved);

        if (allApproved) {
            offer.setOfferStatus(Status.APPROVED);
            offer.setApprovedAt(LocalDateTime.now());
            offer.setApprovedBy(approverEmail);
        }

        return offerRepository.save(offer);
    }

    /**
     * Reject Approval
     */
    @Override
    public Offer rejectApproval(Long offerId, String approverEmail, String comments) {
        Offer offer = getOfferById(offerId);
        List<ApprovalStep> steps = offer.getApprovalChain();

        if (steps == null || steps.isEmpty()) {
            throw new IllegalStateException("Approval chain not configured");
        }

        offer.setOfferStatus(Status.REJECTED);
        offer.setRejectedBy(approverEmail);
        offer.setRejectedAt(LocalDateTime.now());
        offer.setRejectionReason(comments);
        offer.setCurrentApprovalStep(0);

        return offerRepository.save(offer);
    }

    /**
     * Get Approval Chain
     */
    @Override
    public List<ApprovalStepDto> getApprovalChain(Long offerId) {
        Offer offer = getOfferById(offerId);
        List<ApprovalStep> steps = offer.getApprovalChain();
        if (steps == null) {
            return List.of();
        }
        return steps.stream()
                .map(step -> {
                    ApprovalStepDto dto = new ApprovalStepDto();
                    dto.setStepOrder(step.getOrderNumber());
                    dto.setApproverEmail(step.getApproverEmail());
                    dto.setApproved(step.getApproved());
                    dto.setApprovedBy(step.getApprovedBy());
                    return dto;
                }).toList();
    }

    @Override
    public void handleDocuSignWebhook(DocuSignWebhookDto dto) {

        if (!"completed".equalsIgnoreCase(dto.getStatus())) {
            return;
        }

        Offer offer =
                offerRepository
                        .findByDocuSignId(dto.getEnvelopeId())
                        .orElseThrow(
                                () -> new EntityNotFoundException("Offer not found")
                        );

        offer.setOfferStatus(Status.SIGNED);
        offer.setSignedAt(LocalDateTime.now());

        offerRepository.save(offer);
    }
}