package kg.ortcrm.repository;

import kg.ortcrm.entity.PaymentSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PaymentScheduleRepository extends JpaRepository<PaymentSchedule, Long> {

    List<PaymentSchedule> findByAgreementIdOrderByInstallmentNumberAsc(Long agreementId);

    List<PaymentSchedule> findByAgreementStudentCourseStudentIdOrderByDueDateAsc(Long studentId);

    @Query("SELECT ps FROM PaymentSchedule ps " +
            "WHERE ps.agreement.studentCourse.student.id = :studentId " +
            "AND ps.paidAmount < ps.amountDue " +
            "ORDER BY ps.dueDate ASC, ps.installmentNumber ASC")
    List<PaymentSchedule> findOpenByStudentId(@Param("studentId") Long studentId);

    @Query("SELECT ps FROM PaymentSchedule ps " +
            "WHERE ps.paidAmount < ps.amountDue " +
            "AND ps.dueDate < :today " +
            "ORDER BY ps.dueDate ASC")
    List<PaymentSchedule> findOverdue(@Param("today") LocalDate today);

    @Query("SELECT COUNT(ps) FROM PaymentSchedule ps " +
            "WHERE ps.paidAmount < ps.amountDue " +
            "AND ps.dueDate < :today")
    long countOverdue(@Param("today") LocalDate today);

    @Query("SELECT COUNT(ps) FROM PaymentSchedule ps " +
            "WHERE ps.paidAmount < ps.amountDue " +
            "AND ps.dueDate >= :today")
    long countPending(@Param("today") LocalDate today);

    void deleteByAgreementId(Long agreementId);
}
