package com.forge.talentacquisitionengine.offerService.offerTemplate.service;

import com.forge.talentacquisitionengine.offerService.offer.entity.Offer;

public interface OfferTemplateService {

    String generateOfferContent(
            Offer offer,
            String templateName
    );
}