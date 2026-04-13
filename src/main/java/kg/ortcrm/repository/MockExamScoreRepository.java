package kg.ortcrm.repository;

import kg.ortcrm.entity.MockExamScore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MockExamScoreRepository extends JpaRepository<MockExamScore, Long> {

    List<MockExamScore> findByMockExamId(Long mockExamId);

    List<MockExamScore> findByStudentId(Long studentId);

    List<MockExamScore> findByStudentIdAndSubjectId(Long studentId, Long subjectId);

    Optional<MockExamScore> findByMockExamIdAndStudentIdAndSubjectId(Long mockExamId, Long studentId, Long subjectId);

    @Query("SELECT AVG(m.score) FROM MockExamScore m WHERE m.student.id = :studentId")
    Double findAverageScoreByStudentId(@Param("studentId") Long studentId);

    @Query("SELECT AVG(m.score) FROM MockExamScore m WHERE m.student.id = :studentId AND m.subject.id = :subjectId")
    Double findAverageScoreByStudentIdAndSubjectId(@Param("studentId") Long studentId, @Param("subjectId") Long subjectId);

    @Query("SELECT AVG(m.score) FROM MockExamScore m")
    Double findAverageScore();
}
