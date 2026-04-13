package kg.ortcrm.service;

import kg.ortcrm.dto.mockexam.MockExamRequest;
import kg.ortcrm.dto.mockexam.MockExamResponse;
import kg.ortcrm.dto.mockexam.MockExamScoreRequest;
import kg.ortcrm.dto.mockexam.MockExamScoreResponse;
import kg.ortcrm.entity.MockExam;
import kg.ortcrm.entity.MockExamScore;
import kg.ortcrm.entity.Student;
import kg.ortcrm.entity.Subject;
import kg.ortcrm.exception.ResourceNotFoundException;
import kg.ortcrm.mapper.MockExamMapper;
import kg.ortcrm.repository.MockExamRepository;
import kg.ortcrm.repository.MockExamScoreRepository;
import kg.ortcrm.repository.StudentRepository;
import kg.ortcrm.repository.SubjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MockExamService {

    private final MockExamRepository mockExamRepository;
    private final MockExamScoreRepository mockExamScoreRepository;
    private final StudentRepository studentRepository;
    private final SubjectRepository subjectRepository;
    private final MockExamMapper mockExamMapper;

    public List<MockExamResponse> findAll() {
        List<MockExam> mockExams = mockExamRepository.findAllOrderByDateDesc();
        return mockExamMapper.toResponseList(mockExams);
    }

    public MockExamResponse findById(Long id) {
        MockExam mockExam = mockExamRepository.findByIdWithScores(id)
                .orElseThrow(() -> new ResourceNotFoundException("Mock exam not found with id: " + id));
        return mockExamMapper.toResponse(mockExam);
    }

    @Transactional
    public MockExamResponse create(MockExamRequest request) {
        MockExam mockExam = MockExam.builder()
                .title(request.getTitle())
                .examDate(request.getExamDate())
                .build();

        MockExam savedMockExam = mockExamRepository.save(mockExam);
        return mockExamMapper.toResponse(savedMockExam);
    }

    @Transactional
    public MockExamResponse update(Long id, MockExamRequest request) {
        MockExam mockExam = mockExamRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Mock exam not found with id: " + id));

        mockExam.setTitle(request.getTitle());
        mockExam.setExamDate(request.getExamDate());

        MockExam updatedMockExam = mockExamRepository.save(mockExam);
        return mockExamMapper.toResponse(updatedMockExam);
    }

    @Transactional
    public void delete(Long id) {
        if (!mockExamRepository.existsById(id)) {
            throw new ResourceNotFoundException("Mock exam not found with id: " + id);
        }
        mockExamRepository.deleteById(id);
    }

    @Transactional
    public List<MockExamScoreResponse> addScores(Long mockExamId, MockExamScoreRequest request) {
        MockExam mockExam = mockExamRepository.findById(mockExamId)
                .orElseThrow(() -> new ResourceNotFoundException("Mock exam not found with id: " + mockExamId));

        List<MockExamScore> savedScores = new ArrayList<>();

        for (MockExamScoreRequest.ScoreEntry entry : request.getScores()) {
            Student student = studentRepository.findById(entry.getStudentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + entry.getStudentId()));

            Subject subject = subjectRepository.findById(entry.getSubjectId())
                    .orElseThrow(() -> new ResourceNotFoundException("Subject not found with id: " + entry.getSubjectId()));

            Optional<MockExamScore> existingScore = mockExamScoreRepository
                    .findByMockExamIdAndStudentIdAndSubjectId(mockExamId, entry.getStudentId(), entry.getSubjectId());

            MockExamScore score;
            if (existingScore.isPresent()) {
                score = existingScore.get();
                score.setScore(entry.getScore());
            } else {
                score = MockExamScore.builder()
                        .mockExam(mockExam)
                        .student(student)
                        .subject(subject)
                        .score(entry.getScore())
                        .build();
            }

            savedScores.add(mockExamScoreRepository.save(score));
        }

        return mockExamMapper.toScoreResponseList(savedScores);
    }

    public List<MockExamScoreResponse> findScoresByMockExamId(Long mockExamId) {
        List<MockExamScore> scores = mockExamScoreRepository.findByMockExamId(mockExamId);
        return mockExamMapper.toScoreResponseList(scores);
    }

    public List<MockExamScoreResponse> findScoresByStudentId(Long studentId) {
        List<MockExamScore> scores = mockExamScoreRepository.findByStudentId(studentId);
        return mockExamMapper.toScoreResponseList(scores);
    }
}
