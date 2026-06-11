package com.forge.talentacquisitionengine.candidateService.externalCandidate.mapper;


import com.forge.talentacquisitionengine.candidateService.externalCandidate.dto.CertificationDetailDto;
import com.forge.talentacquisitionengine.candidateService.externalCandidate.dto.EducationDetailDto;
import com.forge.talentacquisitionengine.candidateService.externalCandidate.dto.ExternalCandidateDto;
import com.forge.talentacquisitionengine.candidateService.externalCandidate.entity.Address;
import com.forge.talentacquisitionengine.candidateService.externalCandidate.entity.CertificationDetail;
import com.forge.talentacquisitionengine.candidateService.externalCandidate.entity.EducationDetail;
import com.forge.talentacquisitionengine.candidateService.externalCandidate.entity.ExternalCandidate;
import com.forge.talentacquisitionengine.candidateService.externalCandidate.entity.SkillDetail;

import java.util.Set;
import java.util.stream.Collectors;

public class ExternalCandidateMapper {

    private ExternalCandidateMapper() {
    }

    public static ExternalCandidate dtoToEntity(ExternalCandidateDto dto) {
        if (dto == null) {
            return null;
        }

        ExternalCandidate entity = new ExternalCandidate();
        entity.setFirstName(dto.getFirstName());
        entity.setLastName(dto.getLastName());
        entity.setEmail(dto.getEmail());
        entity.setPhoneNumber(dto.getPhoneNumber());
        entity.setDateOfBirth(dto.getDateOfBirth());
        entity.setGender(dto.getGender());
        if (dto.getAddress() != null) {
            Address address = new Address();
            address.setStreet1(dto.getAddress());
            address.setStreet2("");
            address.setCity("");
            address.setState("");
            address.setCountry("");
            address.setZipCode(0L);
            address.setCandidate(entity);
            entity.setAddress(address);
        }
        entity.setTotalExperienceYears(dto.getTotalExperienceYears());
        entity.setTotalGapYears(dto.getTotalGapYears());
        entity.setCompanyName(dto.getCompanyName());
        entity.setDesignation(dto.getDesignation());
        entity.setCurrentCtc(dto.getCurrentCtc());
        entity.setExpectedCtc(dto.getExpectedCtc());
        entity.setNoticePeriodDays(dto.getNoticePeriodDays());
        entity.setWillingToRelocate(dto.getWillingToRelocate());
        entity.setFreeNotes(dto.getFreeNotes());
        entity.setSource(dto.getSource());

        if (dto.getSkills() != null && !dto.getSkills().isEmpty()) {
            Set<SkillDetail> skills = dto.getSkills()
                    .stream()
                    .map(skillName -> {
                        SkillDetail skill = new SkillDetail();
                        skill.setSkillName(skillName);
                        skill.setCandidate(entity);
                        return skill;
                    })
                    .collect(Collectors.toSet());

            entity.setSkills(skills);
        }

        if (dto.getEducationDetails() != null && !dto.getEducationDetails().isEmpty()) {
            Set<EducationDetail> educationDetails = dto.getEducationDetails()
                    .stream()
                    .map(ExternalCandidateMapper::educationDtoToEntity)
                    .peek(education -> education.setCandidate(entity))
                    .collect(Collectors.toSet());

            entity.setEducationDetails(educationDetails);
        }

        if (dto.getCertificationDetails() != null && !dto.getCertificationDetails().isEmpty()) {
            Set<CertificationDetail> certificationDetails = dto.getCertificationDetails()
                    .stream()
                    .map(ExternalCandidateMapper::certificationDtoToEntity)
                    .peek(certification -> certification.setCandidate(entity))
                    .collect(Collectors.toSet());

            entity.setCertificationDetails(certificationDetails);
        }

        return entity;
    }

    public static ExternalCandidateDto entityToDto(ExternalCandidate entity) {
        if (entity == null) {
            return null;
        }

        ExternalCandidateDto dto = new ExternalCandidateDto();
        dto.setFirstName(entity.getFirstName());
        dto.setLastName(entity.getLastName());
        dto.setEmail(entity.getEmail());
        dto.setPhoneNumber(entity.getPhoneNumber());
        dto.setDateOfBirth(entity.getDateOfBirth());
        dto.setGender(entity.getGender());
        if (entity.getAddress() != null) {
            dto.setAddress(entity.getAddress().getStreet1());
        }
        dto.setTotalExperienceYears(entity.getTotalExperienceYears());
        dto.setTotalGapYears(entity.getTotalGapYears());
        dto.setCompanyName(entity.getCompanyName());
        dto.setDesignation(entity.getDesignation());
        dto.setCurrentCtc(entity.getCurrentCtc());
        dto.setExpectedCtc(entity.getExpectedCtc());
        dto.setNoticePeriodDays(entity.getNoticePeriodDays());
        dto.setWillingToRelocate(entity.getWillingToRelocate());
        dto.setFreeNotes(entity.getFreeNotes());
        dto.setSource(entity.getSource());

        if (entity.getSkills() != null && !entity.getSkills().isEmpty()) {
            dto.setSkills(
                    entity.getSkills()
                            .stream()
                            .map(SkillDetail::getSkillName)
                            .collect(Collectors.toList())
            );
        }

        if (entity.getEducationDetails() != null && !entity.getEducationDetails().isEmpty()) {
            dto.setEducationDetails(
                    entity.getEducationDetails()
                            .stream()
                            .map(ExternalCandidateMapper::educationEntityToDto)
                            .collect(Collectors.toList())
            );
        }

        if (entity.getCertificationDetails() != null && !entity.getCertificationDetails().isEmpty()) {
            dto.setCertificationDetails(
                    entity.getCertificationDetails()
                            .stream()
                            .map(ExternalCandidateMapper::certificationEntityToDto)
                            .collect(Collectors.toList())
            );
        }

        return dto;
    }

    private static EducationDetail educationDtoToEntity(EducationDetailDto dto) {
        if (dto == null) {
            return null;
        }

        EducationDetail entity = new EducationDetail();
        entity.setDegree(dto.getDegree());
        entity.setSpecialization(dto.getSpecialization());
        entity.setInstitutionName(dto.getInstitutionName());
        entity.setStartYear(dto.getStartYear());
        entity.setEndYear(dto.getEndYear());
        entity.setPercentage(dto.getPercentage());

        return entity;
    }

    private static EducationDetailDto educationEntityToDto(EducationDetail entity) {
        if (entity == null) {
            return null;
        }

        EducationDetailDto dto = new EducationDetailDto();
        dto.setDegree(entity.getDegree());
        dto.setSpecialization(entity.getSpecialization());
        dto.setInstitutionName(entity.getInstitutionName());
        dto.setStartYear(entity.getStartYear());
        dto.setEndYear(entity.getEndYear());
        dto.setPercentage(entity.getPercentage());

        return dto;
    }

    private static CertificationDetail certificationDtoToEntity(CertificationDetailDto dto) {
        if (dto == null) {
            return null;
        }

        CertificationDetail entity = new CertificationDetail();
        entity.setCertificateName(dto.getCertificateName());
        entity.setIssuingOrganization(dto.getIssuingOrganization());
        entity.setIssuedDate(dto.getIssuedDate());
        entity.setCredentialUrl(dto.getCredentialUrl());

        return entity;
    }

    private static CertificationDetailDto certificationEntityToDto(CertificationDetail entity) {
        if (entity == null) {
            return null;
        }

        CertificationDetailDto dto = new CertificationDetailDto();
        dto.setCertificateName(entity.getCertificateName());
        dto.setIssuingOrganization(entity.getIssuingOrganization());
        dto.setIssuedDate(entity.getIssuedDate());
        dto.setCredentialUrl(entity.getCredentialUrl());

        return dto;
    }
}