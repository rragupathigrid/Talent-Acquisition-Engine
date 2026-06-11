package com.forge.talentacquisitionengine.candidateService.externalCandidate.service;

import com.forge.talentacquisitionengine.candidateService.externalCandidate.dto.CandidateResponse;
import com.forge.talentacquisitionengine.candidateService.externalCandidate.dto.ExternalCandidateDto;
import com.forge.talentacquisitionengine.candidateService.externalCandidate.entity.ExternalCandidate;
import com.forge.talentacquisitionengine.candidateService.externalCandidate.event.CandidateCreatedEvent;
import com.forge.talentacquisitionengine.candidateService.externalCandidate.kafka.CandidateEventProducer;
import com.forge.talentacquisitionengine.candidateService.externalCandidate.mapper.ExternalCandidateMapper;
import com.forge.talentacquisitionengine.candidateService.externalCandidate.repository.ExternalCandidateRepository;
import com.forge.talentacquisitionengine.candidateService.externalCandidate.utility.HashUtil;
import com.forge.talentacquisitionengine.exception.BusinessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class ExternalCandidateService {

    private final ExternalCandidateRepository externalCandidateRepository;
    private final HashUtil hashUtil;
    private final CandidateEventProducer candidateEventProducer;

    public ExternalCandidateService(
            ExternalCandidateRepository externalCandidateRepository,
            HashUtil hashUtil,
            CandidateEventProducer candidateEventProducer
    ) {
        this.externalCandidateRepository = externalCandidateRepository;
        this.hashUtil = hashUtil;
        this.candidateEventProducer = candidateEventProducer;
    }

    @Transactional
    public CandidateResponse createCandidate(ExternalCandidateDto dto) {

        String email = normalizeEmail(dto.getEmail());
        String phone = normalizePhone(dto.getPhoneNumber());

        String emailHash = hashUtil.sha256(email);
        String phoneHash = hashUtil.sha256(phone);

        Optional<ExternalCandidate> existingCandidate =
                externalCandidateRepository.findActiveDuplicate(emailHash, phoneHash);

        if (existingCandidate.isPresent()) {
            throw new BusinessException(
                    HttpStatus.CONFLICT,
                    "Candidate already exists with same email or phone number"
            );
        }

        ExternalCandidate candidate = ExternalCandidateMapper.dtoToEntity(dto);

        candidate.setCandidateId(null);
        candidate.setEmail(email);
        candidate.setPhoneNumber(phone);
        candidate.setEmailHash(emailHash);
        candidate.setPhoneHash(phoneHash);

        candidate.setIsDeleted(false);
        candidate.setDeletedAt(null);
        candidate.setDeletedBy(null);
        candidate.setDeleteReason(null);

        candidate.setPiiAnonymized(false);
        candidate.setPiiAnonymizedAt(null);

        candidate.setGdprDeleteRequested(false);
        candidate.setGdprDeleteRequestedAt(null);
        candidate.setGdprDeleteDueAt(null);

        ExternalCandidate savedCandidate =
                externalCandidateRepository.save(candidate);

        CandidateCreatedEvent event =
                CandidateCreatedEvent.builder()
                        .eventType("candidate.created")
                        .candidateId(savedCandidate.getCandidateId())
                        .firstName(savedCandidate.getFirstName())
                        .lastName(savedCandidate.getLastName())
                        .email(savedCandidate.getEmail())
                        .build();

        candidateEventProducer.publishCandidateCreatedEvent(event);

        return CandidateResponse.builder()
                .status(HttpStatus.CREATED.value())
                .message("Candidate created successfully")
                .duplicate(false)
                .build();
    }

    @Transactional
    public CandidateResponse updateCandidate(Long candidateId, ExternalCandidateDto dto) {

        ExternalCandidate existingCandidate =
                externalCandidateRepository.findWithDetailsByCandidateIdAndIsDeletedFalse(candidateId)
                        .orElseThrow(() -> new BusinessException(
                                HttpStatus.NOT_FOUND,
                                "Candidate not found"
                        ));

        String email = normalizeEmail(dto.getEmail());
        String phone = normalizePhone(dto.getPhoneNumber());

        String emailHash = hashUtil.sha256(email);
        String phoneHash = hashUtil.sha256(phone);

        Optional<ExternalCandidate> duplicateCandidate =
                externalCandidateRepository.findDuplicateForUpdate(
                        candidateId,
                        emailHash,
                        phoneHash
                );

        if (duplicateCandidate.isPresent()) {
            throw new BusinessException(
                    HttpStatus.CONFLICT,
                    "Candidate already exists with same email or phone number"
            );
        }

        ExternalCandidate updatedCandidate = ExternalCandidateMapper.dtoToEntity(dto);

        existingCandidate.setFirstName(updatedCandidate.getFirstName());
        existingCandidate.setLastName(updatedCandidate.getLastName());
        existingCandidate.setEmail(email);
        existingCandidate.setPhoneNumber(phone);
        existingCandidate.setEmailHash(emailHash);
        existingCandidate.setPhoneHash(phoneHash);
        existingCandidate.setDateOfBirth(updatedCandidate.getDateOfBirth());
        existingCandidate.setGender(updatedCandidate.getGender());
        existingCandidate.setAddress(updatedCandidate.getAddress());
        existingCandidate.setTotalExperienceYears(updatedCandidate.getTotalExperienceYears());
        existingCandidate.setTotalGapYears(updatedCandidate.getTotalGapYears());
        existingCandidate.setCompanyName(updatedCandidate.getCompanyName());
        existingCandidate.setDesignation(updatedCandidate.getDesignation());
        existingCandidate.setCurrentCtc(updatedCandidate.getCurrentCtc());
        existingCandidate.setExpectedCtc(updatedCandidate.getExpectedCtc());
        existingCandidate.setNoticePeriodDays(updatedCandidate.getNoticePeriodDays());
        existingCandidate.setWillingToRelocate(updatedCandidate.getWillingToRelocate());
        existingCandidate.setFreeNotes(updatedCandidate.getFreeNotes());
        existingCandidate.setSource(updatedCandidate.getSource());

        if (updatedCandidate.getSkills() != null) {
            updatedCandidate.getSkills().forEach(newSkill -> {

                boolean alreadyExists = existingCandidate.getSkills()
                        .stream()
                        .anyMatch(existingSkill ->
                                existingSkill.getSkillName()
                                        .equalsIgnoreCase(newSkill.getSkillName())
                        );

                if (!alreadyExists) {
                    newSkill.setCandidate(existingCandidate);
                    existingCandidate.getSkills().add(newSkill);
                }
            });
        }

        if (updatedCandidate.getEducationDetails() != null) {
            updatedCandidate.getEducationDetails().forEach(newEducation -> {

                boolean alreadyExists = existingCandidate.getEducationDetails()
                        .stream()
                        .anyMatch(existingEducation ->
                                existingEducation.getDegree().equalsIgnoreCase(newEducation.getDegree())
                                        && existingEducation.getSpecialization().equalsIgnoreCase(newEducation.getSpecialization())
                                        && existingEducation.getInstitutionName().equalsIgnoreCase(newEducation.getInstitutionName())
                                        && existingEducation.getStartYear().equals(newEducation.getStartYear())
                                        && existingEducation.getEndYear().equals(newEducation.getEndYear())
                        );

                if (!alreadyExists) {
                    newEducation.setCandidate(existingCandidate);
                    existingCandidate.getEducationDetails().add(newEducation);
                }
            });
        }

        if (updatedCandidate.getCertificationDetails() != null) {
            updatedCandidate.getCertificationDetails().forEach(newCertification -> {

                boolean alreadyExists = existingCandidate.getCertificationDetails()
                        .stream()
                        .anyMatch(existingCertification ->
                                existingCertification.getCertificateName().equalsIgnoreCase(newCertification.getCertificateName())
                                        && existingCertification.getIssuingOrganization().equalsIgnoreCase(newCertification.getIssuingOrganization())
                                        && existingCertification.getIssuedDate().equals(newCertification.getIssuedDate())
                        );

                if (!alreadyExists) {
                    newCertification.setCandidate(existingCandidate);
                    existingCandidate.getCertificationDetails().add(newCertification);
                }
            });
        }

        externalCandidateRepository.save(existingCandidate);

        return CandidateResponse.builder()
                .status(HttpStatus.OK.value())
                .message("Candidate updated successfully")
                .duplicate(false)
                .build();
    }

    @Transactional(readOnly = true)
    public ExternalCandidateDto getByCandidateId(Long candidateId) {

        ExternalCandidate candidate =
                externalCandidateRepository.findWithDetailsByCandidateIdAndIsDeletedFalse(candidateId)
                        .orElseThrow(() -> new BusinessException(
                                HttpStatus.NOT_FOUND,
                                "Candidate not found"
                        ));

        return ExternalCandidateMapper.entityToDto(candidate);
    }

    @Transactional
    public void deleteById(Long candidateId) {

        ExternalCandidate candidate =
                externalCandidateRepository.findByCandidateIdAndIsDeletedFalse(candidateId)
                        .orElseThrow(() -> new BusinessException(
                                HttpStatus.NOT_FOUND,
                                "Candidate not found"
                        ));

        candidate.setIsDeleted(true);
        candidate.setDeletedAt(LocalDateTime.now());

        externalCandidateRepository.save(candidate);
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase();
    }

    private String normalizePhone(String phone) {
        return phone.trim();
    }
}