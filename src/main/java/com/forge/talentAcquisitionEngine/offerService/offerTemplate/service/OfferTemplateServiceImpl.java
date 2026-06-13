package com.forge.talentacquisitionengine.offerService.offerTemplate.service;

import com.forge.talentacquisitionengine.offerService.offer.entity.Offer;
import com.forge.talentacquisitionengine.offerService.offer.entity.OfferTemplate;
import com.forge.talentacquisitionengine.offerService.offer.repository.OfferTemplateRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OfferTemplateServiceImpl
        implements OfferTemplateService {

    private final OfferTemplateRepository repository;

    @Override
    public String generateOfferContent(
            Offer offer,
            String templateName
    ) {

        OfferTemplate template =
                repository.findByTemplateName(
                                templateName
                        )
                        .orElseThrow(() ->
                                new EntityNotFoundException(
                                        "Template not found"
                                )
                        );

        String content =
                template.getTemplateContent();

        return content
                .replace(
                        "{{candidateName}}",
                        offer.getApplication()
                                .getCandidate()
                                .getFirstName()
                                + " "
                                +
                                offer.getApplication()
                                        .getCandidate()
                                        .getLastName()
                )
                .replace(
                        "{{role}}",
                        offer.getRole()
                )
                .replace(
                        "{{baseSalary}}",
                        String.valueOf(
                                offer.getBaseSalary()
                        )
                )
                .replace(
                        "{{bonus}}",
                        String.valueOf(
                                offer.getBonus()
                        )
                )
                .replace(
                        "{{equity}}",
                        String.valueOf(
                                offer.getEquity()
                        )
                )
                .replace(
                        "{{joiningDate}}",
                        String.valueOf(
                                offer.getJoiningDate()
                        )
                );
    }
}