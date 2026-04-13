package kg.ortcrm.mapper;

import kg.ortcrm.dto.subject.SubjectRequest;
import kg.ortcrm.dto.subject.SubjectResponse;
import kg.ortcrm.entity.Subject;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SubjectMapper {

    @Mapping(target = "id", ignore = true)
    Subject toEntity(SubjectRequest request);

    SubjectResponse toResponse(Subject subject);

    List<SubjectResponse> toResponseList(List<Subject> subjects);
}
