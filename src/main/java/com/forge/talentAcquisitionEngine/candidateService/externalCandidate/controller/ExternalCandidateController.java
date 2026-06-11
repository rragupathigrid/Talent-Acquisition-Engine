package com.forge.talentacquisitionengine.candidateService.externalCandidate.controller;

import com.forge.talentacquisitionengine.candidateService.externalCandidate.dto.CandidateResponse;
import com.forge.talentacquisitionengine.candidateService.externalCandidate.dto.ExternalCandidateDto;
import com.forge.talentacquisitionengine.candidateService.externalCandidate.service.ExternalCandidateService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/external-candidates")
public class ExternalCandidateController {

    private final ExternalCandidateService externalCandidateService;

    public ExternalCandidateController(ExternalCandidateService externalCandidateService) {
        this.externalCandidateService = externalCandidateService;
    }

    @PostMapping
    public CandidateResponse createCandidate(@Valid @RequestBody ExternalCandidateDto externalCandidateDto){
        CandidateResponse response =
                externalCandidateService.createCandidate(externalCandidateDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(response).getBody();
    }

    @PutMapping("/{candidateId}")
    public ResponseEntity<CandidateResponse> updateCandidate(
            @PathVariable Long candidateId,
            @Valid @RequestBody ExternalCandidateDto externalCandidateDto
    ) {
        CandidateResponse response =
                externalCandidateService.updateCandidate(candidateId, externalCandidateDto);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{candidateId}")
    public ResponseEntity<ExternalCandidateDto> getCandidateById(
            @PathVariable Long candidateId
    ) {
        return ResponseEntity.ok(
                externalCandidateService.getByCandidateId(candidateId)
        );
    }

    @DeleteMapping("/{candidateId}")
    public ResponseEntity<Void> deleteById(
            @PathVariable Long candidateId
    ) {
        externalCandidateService.deleteById(candidateId);
        return ResponseEntity.noContent().build();
    }
}