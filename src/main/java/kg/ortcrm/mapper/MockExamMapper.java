package kg.ortcrm.mapper;

import kg.ortcrm.dto.mockexam.MockExamResponse;
import kg.ortcrm.dto.mockexam.MockExamScoreResponse;
import kg.ortcrm.entity.MockExam;
import kg.ortcrm.entity.MockExamScore;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MockExamMapper {

    @Mapping(target = "participantCount", expression = "java(mockExam.getScores() != null ? (int) mockExam.getScores().stream().map(s -> s.getStudent().getId()).distinct().count() : 0)")
    @Mapping(target = "scores", source = "scores")
    MockExamResponse toResponse(MockExam mockExam);

    List<MockExamResponse> toResponseList(List<MockExam> mockExams);

    @Mapping(target = "mockExamId", source = "mockExam.id")
    @Mapping(target = "mockExamTitle", source = "mockExam.title")
    @Mapping(target = "examDate", source = "mockExam.examDate")
    @Mapping(target = "studentId", source = "student.id")
    @Mapping(target = "studentName", source = "student.fullName")
    @Mapping(target = "subjectId", source = "subject.id")
    @Mapping(target = "subjectName", source = "subject.name")
    MockExamScoreResponse toScoreResponse(MockExamScore score);

    List<MockExamScoreResponse> toScoreResponseList(List<MockExamScore> scores);
}
