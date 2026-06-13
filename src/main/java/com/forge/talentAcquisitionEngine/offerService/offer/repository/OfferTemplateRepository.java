package com.forge.talentacquisitionengine.offerService.offer.repository;

import com.forge.talentacquisitionengine.offerService.offer.entity.OfferTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OfferTemplateRepository extends JpaRepository<OfferTemplate, Long> {

    Optional<OfferTemplate> findByActiveTrue();

    Optional<OfferTemplate> findByTemplateName(String templateName);
}