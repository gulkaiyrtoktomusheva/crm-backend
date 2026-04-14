package kg.ortcrm.repository;

import kg.ortcrm.entity.PaymentAgreement;
import kg.ortcrm.entity.enums.PaymentAgreementStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentAgreementRepository extends JpaRepository<PaymentAgreement, Long> {

    List<PaymentAgreement> findByStudentCourseStudentIdOrderByCreatedAtDesc(Long studentId);

    Optional<PaymentAgreement> findByStudentCourseId(Long studentCourseId);

    boolean existsByStudentCourseIdAndStatusIn(Long studentCourseId, Collection<PaymentAgreementStatus> statuses);
}
