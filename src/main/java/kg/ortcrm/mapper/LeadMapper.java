package kg.ortcrm.mapper;

import kg.ortcrm.dto.lead.LeadRequest;
import kg.ortcrm.dto.lead.LeadResponse;
import kg.ortcrm.entity.Lead;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface LeadMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "assignedTo", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Lead toEntity(LeadRequest request);

    @Mapping(target = "assignedToId", source = "assignedTo.id")
    @Mapping(target = "assignedToName", source = "assignedTo.fullName")
    LeadResponse toResponse(Lead lead);

    List<LeadResponse> toResponseList(List<Lead> leads);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "assignedTo", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    void updateEntity(@MappingTarget Lead lead, LeadRequest request);
}
