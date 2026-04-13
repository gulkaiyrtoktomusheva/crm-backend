package kg.ortcrm.repository;

import kg.ortcrm.entity.Student;
import kg.ortcrm.entity.enums.StudentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    Page<Student> findByStatus(StudentStatus status, Pageable pageable);

    @Query("SELECT DISTINCT s FROM Student s " +
           "LEFT JOIN s.groups g " +
           "WHERE (:status IS NULL OR s.status = :status) " +
           "AND (:groupId IS NULL OR g.id = :groupId) " +
           "AND (:search = '' OR LOWER(s.fullName) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Student> findByFilters(@Param("status") StudentStatus status,
                                @Param("groupId") Long groupId,
                                @Param("search") String search,
                                Pageable pageable);

    @Query("SELECT s FROM Student s JOIN s.groups g WHERE g.id = :groupId")
    List<Student> findByGroupId(@Param("groupId") Long groupId);

    long countByStatus(StudentStatus status);
}
