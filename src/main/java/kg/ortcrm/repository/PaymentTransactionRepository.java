package kg.ortcrm.repository;

import kg.ortcrm.entity.PaymentTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface PaymentTransactionRepository extends JpaRepository<PaymentTransaction, Long> {

    List<PaymentTransaction> findByAgreementStudentCourseStudentIdOrderByPaidAtDesc(Long studentId);

    @Query("SELECT COALESCE(SUM(pt.amount), 0) FROM PaymentTransaction pt WHERE pt.student.id = :studentId")
    BigDecimal sumPaidAmountByStudentId(@Param("studentId") Long studentId);

    @Query("SELECT COALESCE(SUM(pt.amount), 0) FROM PaymentTransaction pt")
    BigDecimal sumAllPaidAmount();

    boolean existsByAgreementId(Long agreementId);
}
