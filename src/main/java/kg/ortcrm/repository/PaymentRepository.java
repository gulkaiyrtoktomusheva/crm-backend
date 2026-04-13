package kg.ortcrm.repository;

import kg.ortcrm.entity.Payment;
import kg.ortcrm.entity.enums.PaymentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    List<Payment> findByStudentId(Long studentId);

    List<Payment> findByStudentIdOrderByDueDateAsc(Long studentId);

    Page<Payment> findByStatus(PaymentStatus status, Pageable pageable);

    List<Payment> findByStatusAndDueDateBefore(PaymentStatus status, LocalDate date);

    @Query("SELECT SUM(p.amount) FROM Payment p WHERE p.student.id = :studentId AND p.status = 'PAID'")
    BigDecimal sumPaidAmountByStudentId(@Param("studentId") Long studentId);

    @Query("SELECT SUM(p.totalDue) FROM Payment p WHERE p.student.id = :studentId")
    BigDecimal sumTotalDueByStudentId(@Param("studentId") Long studentId);

    @Query("SELECT SUM(p.amount) FROM Payment p WHERE p.status = 'PAID'")
    BigDecimal sumAllPaidAmount();

    @Query("SELECT COUNT(p) FROM Payment p WHERE p.status = :status")
    long countByStatus(@Param("status") PaymentStatus status);
}
