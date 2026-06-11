package com.forge.talentacquisitionengine.offerService.offer.service;

import com.forge.talentacquisitionengine.applicationService.application.entity.Application;
import com.forge.talentacquisitionengine.applicationService.application.repository.ApplicationRepository;
import com.forge.talentacquisitionengine.offerService.offer.entity.Offer;
import com.forge.talentacquisitionengine.offerService.offer.enums.Status;
import com.forge.talentacquisitionengine.offerService.offer.repository.OfferRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OfferServiceImpl implements OfferService {

    private final OfferRepository offerRepository;
    private final ApplicationRepository applicationRepository;

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
     * Send Offer
     */
    @Override
    public Offer sendOffer(Long id) {

        Offer offer =
                getOfferById(id);

        /*
         * Business Validation
         */
        if (offer.getOfferStatus() != Status.DRAFT) {

            throw new IllegalStateException(
                    "Only draft offers can be sent"
            );
        }

        offer.setOfferStatus(Status.SENT);

        return offerRepository.save(offer);
    }

    /**
     * Accept Offer
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

        offer.setOfferStatus(Status.APPROVED);

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
         * Prevent Expiring Accepted Offer
         */
        if (offer.getOfferStatus() == Status.APPROVED) {

            throw new IllegalStateException(
                    "Accepted offer cannot expire"
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


}