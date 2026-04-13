package kg.ortcrm.mapper;

import kg.ortcrm.dto.group.GroupRequest;
import kg.ortcrm.dto.group.GroupResponse;
import kg.ortcrm.entity.Group;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface GroupMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "subject", ignore = true)
    @Mapping(target = "teacher", ignore = true)
    @Mapping(target = "students", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Group toEntity(GroupRequest request);

    @Mapping(target = "subjectId", source = "subject.id")
    @Mapping(target = "subjectName", source = "subject.name")
    @Mapping(target = "teacherId", source = "teacher.id")
    @Mapping(target = "teacherName", source = "teacher.fullName")
    @Mapping(target = "studentCount", expression = "java(group.getStudents() != null ? group.getStudents().size() : 0)")
    GroupResponse toResponse(Group group);

    List<GroupResponse> toResponseList(List<Group> groups);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "subject", ignore = true)
    @Mapping(target = "teacher", ignore = true)
    @Mapping(target = "students", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    void updateEntity(@MappingTarget Group group, GroupRequest request);
}
