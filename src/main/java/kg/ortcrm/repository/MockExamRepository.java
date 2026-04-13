package kg.ortcrm.repository;

import kg.ortcrm.entity.MockExam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MockExamRepository extends JpaRepository<MockExam, Long> {

    @Query("SELECT m FROM MockExam m LEFT JOIN FETCH m.scores WHERE m.id = :id")
    Optional<MockExam> findByIdWithScores(@Param("id") Long id);

    @Query("SELECT m FROM MockExam m ORDER BY m.examDate DESC")
    List<MockExam> findAllOrderByDateDesc();
}
