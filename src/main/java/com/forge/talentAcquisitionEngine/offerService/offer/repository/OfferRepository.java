package com.forge.talentacquisitionengine.offerService.offer.repository;

import com.forge.talentacquisitionengine.offerService.offer.entity.Offer;
import com.forge.talentacquisitionengine.offerService.offer.enums.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface OfferRepository
        extends JpaRepository<Offer, Long> {

    /**
     * Find offers by application
     */
    Page<Offer> findByApplicationId(
            Long applicationId,
            Pageable pageable
    );

    /**
     * Find offers by status
     */
    Page<Offer> findByOfferStatus(
            Status offerStatus,
            Pageable pageable
    );

    /**
     * Find offers by application + status
     */
    Page<Offer> findByApplicationIdAndOfferStatus(
            Long applicationId,
            Status offerStatus,
            Pageable pageable
    );

    /**
     * Find offers by joining date
     */
    List<Offer> findByJoiningDate(
            LocalDate joiningDate
    );

    /**
     * Find offers joining after a date
     */
    List<Offer> findByJoiningDateAfter(
            LocalDate joiningDate
    );

    /**
     * Find offers joining between dates
     */
    List<Offer> findByJoiningDateBetween(
            LocalDate startDate,
            LocalDate endDate
    );

    /**
     * Count offers by status
     */
    long countByOfferStatus(
            Status offerStatus
    );

    /**
     * Count offers for application
     */
    long countByApplicationId(
            Long applicationId
    );

    /**
     * Check DocuSign envelope existence
     */
    boolean existsByDocuSignId(
            String docuSignId
    );
}