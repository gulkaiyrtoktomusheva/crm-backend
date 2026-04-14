package kg.ortcrm.repository;

import kg.ortcrm.entity.LessonAttendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LessonAttendanceRepository extends JpaRepository<LessonAttendance, Long> {

    List<LessonAttendance> findByLessonIdOrderByStudentCourseStudentFullNameAsc(Long lessonId);

    List<LessonAttendance> findByStudentCourseStudentIdOrderByLessonLessonDateAsc(Long studentId);

    Optional<LessonAttendance> findByLessonIdAndStudentCourseId(Long lessonId, Long studentCourseId);

    @Query("SELECT COUNT(la) FROM LessonAttendance la WHERE la.studentCourse.student.id = :studentId AND la.present = true")
    long countPresentByStudentId(@Param("studentId") Long studentId);

    @Query("SELECT COUNT(la) FROM LessonAttendance la WHERE la.studentCourse.student.id = :studentId")
    long countTotalByStudentId(@Param("studentId") Long studentId);

    @Query("SELECT AVG(CASE WHEN la.present = true THEN 100.0 ELSE 0.0 END) FROM LessonAttendance la")
    Double findAverageAttendancePercentage();
}
