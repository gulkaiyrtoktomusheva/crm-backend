package kg.ortcrm.repository;

import kg.ortcrm.entity.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface LessonRepository extends JpaRepository<Lesson, Long> {

    List<Lesson> findByCourseSubjectCourseIdOrderByLessonDateAsc(Long courseId);

    List<Lesson> findByCourseSubjectIdOrderByLessonDateAsc(Long courseSubjectId);

    Optional<Lesson> findByCourseSubjectIdAndLessonDate(Long courseSubjectId, LocalDate lessonDate);
}
