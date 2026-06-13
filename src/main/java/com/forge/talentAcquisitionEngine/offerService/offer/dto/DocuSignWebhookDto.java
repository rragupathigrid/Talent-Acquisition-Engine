package com.forge.talentacquisitionengine.offerService.offer.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DocuSignWebhookDto {

    private String envelopeId;

    private String status;
}