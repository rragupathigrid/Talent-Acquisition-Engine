package com.forge.talentacquisitionengine.offerService.offer.mapper;

import com.forge.talentacquisitionengine.applicationService.application.entity.Application;
import com.forge.talentacquisitionengine.offerService.offer.dto.OfferDto;
import com.forge.talentacquisitionengine.offerService.offer.entity.Offer;

public class OfferMapper {

  private OfferMapper() {
  }

  public static Offer dtoToEntity(
      OfferDto dto,
      Application application
  ) {

    if (dto == null) {
      return null;
    }

    Offer entity = new Offer();

    entity.setId(
        dto.getId()
    );

    entity.setApplication(
        application
    );

    entity.setRole(
        dto.getRole()
    );

    entity.setBaseSalary(
        dto.getBaseSalary()
    );

    entity.setBonus(
        dto.getBonus()
    );

    entity.setEquity(
        dto.getEquity()
    );

    entity.setJoiningDate(
        dto.getJoiningDate()
    );

    entity.setOfferStatus(
        dto.getOfferStatus()
    );

    entity.setEmploymentType(
        dto.getEmploymentType()
    );

    entity.setApprovedBy(
        dto.getApprovedBy()
    );

    entity.setRejectedBy(
        dto.getRejectedBy()
    );

    entity.setApprovedAt(
        dto.getApprovedAt()
    );

    entity.setRejectedAt(
        dto.getRejectedAt()
    );

    entity.setSentAt(
        dto.getSentAt()
    );

    entity.setSignedAt(
        dto.getSignedAt()
    );

    entity.setExpiresAt(
        dto.getExpiresAt()
    );

    entity.setDocuSignId(
        dto.getDocuSignId()
    );

    return entity;
  }


  public static OfferDto entityToDto(
      Offer entity
  ) {

    if (entity == null) {
      return null;
    }

    OfferDto dto = new OfferDto();

    dto.setId(
        entity.getId()
    );

    if (entity.getApplication() != null) {

      dto.setApplicationId(
          entity.getApplication().getId()
      );
    }

    dto.setRole(
        entity.getRole()
    );

    dto.setBaseSalary(
        entity.getBaseSalary()
    );

    dto.setBonus(
        entity.getBonus()
    );

    dto.setEquity(
        entity.getEquity()
    );

    dto.setJoiningDate(
        entity.getJoiningDate()
    );

    dto.setOfferStatus(
        entity.getOfferStatus()
    );

    dto.setEmploymentType(
        entity.getEmploymentType()
    );

    dto.setApprovedBy(
        entity.getApprovedBy()
    );

    dto.setRejectedBy(
        entity.getRejectedBy()
    );

    dto.setApprovedAt(
        entity.getApprovedAt()
    );

    dto.setRejectedAt(
        entity.getRejectedAt()
    );

    dto.setSentAt(
        entity.getSentAt()
    );

    dto.setSignedAt(
        entity.getSignedAt()
    );

    dto.setExpiresAt(
        entity.getExpiresAt()
    );

    dto.setDocuSignId(
        entity.getDocuSignId()
    );

    return dto;
  }
}