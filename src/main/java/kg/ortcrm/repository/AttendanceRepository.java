package kg.ortcrm.repository;

import kg.ortcrm.entity.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    List<Attendance> findByGroupIdAndLessonDate(Long groupId, LocalDate lessonDate);

    List<Attendance> findByStudentId(Long studentId);

    List<Attendance> findByStudentIdAndGroupId(Long studentId, Long groupId);

    Optional<Attendance> findByStudentIdAndGroupIdAndLessonDate(Long studentId, Long groupId, LocalDate lessonDate);

    @Query("SELECT COUNT(a) FROM Attendance a WHERE a.student.id = :studentId AND a.present = true")
    long countPresentByStudentId(@Param("studentId") Long studentId);

    @Query("SELECT COUNT(a) FROM Attendance a WHERE a.student.id = :studentId")
    long countTotalByStudentId(@Param("studentId") Long studentId);

    @Query("SELECT COUNT(a) FROM Attendance a WHERE a.student.id = :studentId AND a.group.id = :groupId AND a.present = true")
    long countPresentByStudentIdAndGroupId(@Param("studentId") Long studentId, @Param("groupId") Long groupId);

    @Query("SELECT COUNT(a) FROM Attendance a WHERE a.student.id = :studentId AND a.group.id = :groupId")
    long countTotalByStudentIdAndGroupId(@Param("studentId") Long studentId, @Param("groupId") Long groupId);

    @Query("SELECT AVG(CASE WHEN a.present = true THEN 100.0 ELSE 0.0 END) FROM Attendance a")
    Double findAverageAttendancePercentage();
}
