package kg.ortcrm.repository;

import kg.ortcrm.entity.StudentCourse;
import kg.ortcrm.entity.enums.StudentCourseStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface StudentCourseRepository extends JpaRepository<StudentCourse, Long> {

    List<StudentCourse> findByStudentIdOrderByCreatedAtDesc(Long studentId);

    boolean existsByStudentIdAndCourseIdAndStatusIn(Long studentId, Long courseId, Collection<StudentCourseStatus> statuses);

    long countByStudentIdAndCourseIdAndStatusIn(Long studentId, Long courseId, Collection<StudentCourseStatus> statuses);
}
