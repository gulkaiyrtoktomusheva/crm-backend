package kg.ortcrm.repository;

import kg.ortcrm.entity.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GroupRepository extends JpaRepository<Group, Long> {

    List<Group> findBySubjectId(Long subjectId);

    List<Group> findByTeacherId(Long teacherId);

    @Query("SELECT g FROM Group g LEFT JOIN FETCH g.students WHERE g.id = :id")
    Optional<Group> findByIdWithStudents(@Param("id") Long id);

    @Query("SELECT g FROM Group g LEFT JOIN FETCH g.subject LEFT JOIN FETCH g.teacher")
    List<Group> findAllWithDetails();
}
