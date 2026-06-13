package com.forge.talentacquisitionengine.applicationService.application.controller;

import com.forge.talentacquisitionengine.applicationService.application.dto.RejectionEmailDraftRequestDto;
import com.forge.talentacquisitionengine.applicationService.application.dto.RejectionEmailDraftResponseDto;
import com.forge.talentacquisitionengine.applicationService.application.service.RejectionEmailDraftService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/applications")
@RequiredArgsConstructor
public class RejectionEmailDraftController {

    private final RejectionEmailDraftService rejectionEmailDraftService;

    @PostMapping("/{applicationId}/rejection-email-draft")
    public ResponseEntity<RejectionEmailDraftResponseDto> generateDraft(
            @PathVariable Long applicationId,
            @Valid @RequestBody RejectionEmailDraftRequestDto request
    ) {
        return ResponseEntity.ok(
                rejectionEmailDraftService.generateDraft(applicationId, request)
        );
    }
}