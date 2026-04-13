package kg.ortcrm.mapper;

import kg.ortcrm.dto.student.StudentRequest;
import kg.ortcrm.dto.student.StudentResponse;
import kg.ortcrm.dto.subject.SubjectResponse;
import kg.ortcrm.entity.Group;
import kg.ortcrm.entity.Student;
import kg.ortcrm.entity.Subject;
import org.mapstruct.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface StudentMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "groups", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Student toEntity(StudentRequest request);

    @Mapping(target = "groupIds", source = "groups", qualifiedByName = "groupsToIds")
    StudentResponse toResponse(Student student);

    List<StudentResponse> toResponseList(List<Student> students);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "groups", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    void updateEntity(@MappingTarget Student student, StudentRequest request);

    @Named("subjectsToResponse")
    default Set<SubjectResponse> subjectsToResponse(Set<Subject> subjects) {
        if (subjects == null) {
            return null;
        }
        return subjects.stream()
                .map(s -> SubjectResponse.builder()
                        .id(s.getId())
                        .name(s.getName())
                .build())
                .collect(Collectors.toSet());
    }

    @Named("groupsToIds")
    default Set<Long> groupsToIds(Set<Group> groups) {
        if (groups == null) {
            return null;
        }
        return groups.stream()
                .map(Group::getId)
                .collect(Collectors.toSet());
    }
}
