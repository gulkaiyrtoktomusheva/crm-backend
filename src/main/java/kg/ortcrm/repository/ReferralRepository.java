package kg.ortcrm.repository;

import kg.ortcrm.entity.Referral;
import kg.ortcrm.entity.enums.ReferralStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReferralRepository extends JpaRepository<Referral, Long> {

    Optional<Referral> findByReferredStudentId(Long referredStudentId);

    List<Referral> findByReferrerStudentIdAndStatusOrderByCreatedAtAsc(Long referrerStudentId, ReferralStatus status);

    List<Referral> findByReferredStudentIdAndStatus(Long referredStudentId, ReferralStatus status);

    @Query("SELECT COALESCE(SUM(r.remainingAmount), 0) FROM Referral r " +
            "WHERE r.referrerStudent.id = :studentId AND r.status = :status")
    BigDecimal sumRemainingAmountByReferrerAndStatus(@Param("studentId") Long studentId,
                                                     @Param("status") ReferralStatus status);
}
