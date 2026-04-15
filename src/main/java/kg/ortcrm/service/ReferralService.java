package kg.ortcrm.service;

import kg.ortcrm.entity.Referral;
import kg.ortcrm.entity.Student;
import kg.ortcrm.entity.enums.ReferralStatus;
import kg.ortcrm.exception.ResourceNotFoundException;
import kg.ortcrm.repository.ReferralRepository;
import kg.ortcrm.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReferralService {

    private final ReferralRepository referralRepository;
    private final StudentRepository studentRepository;

    @Value("${app.referral.discount-amount:1000}")
    private BigDecimal referralDiscountAmount;

    @Transactional
    public void registerReferral(Student referredStudent, Long referrerStudentId) {
        if (referrerStudentId == null) {
            referredStudent.setReferredByStudent(null);
            if (referredStudent.getId() != null) {
                referralRepository.findByReferredStudentId(referredStudent.getId()).ifPresent(existing -> {
                    if (existing.getStatus() != ReferralStatus.REGISTERED) {
                        throw new IllegalArgumentException("Cannot remove referral after reward has been activated");
                    }
                    referralRepository.delete(existing);
                });
            }
            return;
        }
        if (referredStudent.getId() != null && referredStudent.getId().equals(referrerStudentId)) {
            throw new IllegalArgumentException("Student cannot refer themselves");
        }

        Student referrer = studentRepository.findById(referrerStudentId)
                .orElseThrow(() -> new ResourceNotFoundException("Referrer student not found with id: " + referrerStudentId));
        if (referredStudent.getId() != null && referredStudent.getId().equals(referrer.getId())) {
            throw new IllegalArgumentException("Student cannot refer themselves");
        }

        referredStudent.setReferredByStudent(referrer);
        referralRepository.findByReferredStudentId(referredStudent.getId()).ifPresentOrElse(existing -> {
            if (existing.getStatus() != ReferralStatus.REGISTERED) {
                throw new IllegalArgumentException("Cannot change referral after reward has been activated");
            }
            existing.setReferrerStudent(referrer);
            referralRepository.save(existing);
        }, () -> referralRepository.save(Referral.builder()
                .referrerStudent(referrer)
                .referredStudent(referredStudent)
                .rewardAmount(referralDiscountAmount)
                .remainingAmount(referralDiscountAmount)
                .status(ReferralStatus.REGISTERED)
                .build()));
    }

    @Transactional
    public void activateReferralRewardForReferredStudent(Long referredStudentId) {
        List<Referral> referrals = referralRepository.findByReferredStudentIdAndStatus(referredStudentId, ReferralStatus.REGISTERED);
        LocalDateTime now = LocalDateTime.now();
        referrals.forEach(referral -> {
            referral.setStatus(ReferralStatus.AVAILABLE);
            referral.setAvailableAt(now);
        });
        referralRepository.saveAll(referrals);
    }

    @Transactional(readOnly = true)
    public BigDecimal getAvailableDiscountAmount(Long studentId) {
        return referralRepository.sumRemainingAmountByReferrerAndStatus(studentId, ReferralStatus.AVAILABLE);
    }

    @Transactional
    public BigDecimal consumeAvailableDiscount(Long studentId, BigDecimal requestedAmount) {
        if (requestedAmount == null || requestedAmount.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal remainingToApply = requestedAmount;
        List<Referral> referrals = referralRepository.findByReferrerStudentIdAndStatusOrderByCreatedAtAsc(studentId, ReferralStatus.AVAILABLE);
        LocalDateTime now = LocalDateTime.now();

        for (Referral referral : referrals) {
            if (remainingToApply.compareTo(BigDecimal.ZERO) <= 0) {
                break;
            }

            BigDecimal allocation = referral.getRemainingAmount().min(remainingToApply);
            referral.setRemainingAmount(referral.getRemainingAmount().subtract(allocation));
            if (referral.getRemainingAmount().compareTo(BigDecimal.ZERO) == 0) {
                referral.setStatus(ReferralStatus.APPLIED);
                referral.setAppliedAt(now);
            }
            remainingToApply = remainingToApply.subtract(allocation);
        }

        referralRepository.saveAll(referrals);
        return requestedAmount.subtract(remainingToApply);
    }
}
