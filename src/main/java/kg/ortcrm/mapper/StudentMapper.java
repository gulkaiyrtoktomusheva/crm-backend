package kg.ortcrm.mapper;

import kg.ortcrm.dto.student.StudentRequest;
import kg.ortcrm.dto.student.StudentResponse;
import kg.ortcrm.entity.Student;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface StudentMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "groups", ignore = true)
    @Mapping(target = "referredByStudent", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Student toEntity(StudentRequest request);

    @Mapping(target = "referredByStudentId", source = "referredByStudent.id")
    @Mapping(target = "referredByStudentName", source = "referredByStudent.fullName")
    StudentResponse toResponse(Student student);

    List<StudentResponse> toResponseList(List<Student> students);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "groups", ignore = true)
    @Mapping(target = "referredByStudent", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    void updateEntity(@MappingTarget Student student, StudentRequest request);
}
