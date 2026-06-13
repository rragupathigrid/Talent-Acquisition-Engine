package com.forge.talentacquisitionengine.offerService.offer.dto;

import com.forge.talentacquisitionengine.offerService.offer.dto.ApprovalStepDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ApprovalChainRequestDto {

    @NotEmpty(message = "Approval chain is required")
    @Valid
    private List<ApprovalStepDto> approvalSteps;
}