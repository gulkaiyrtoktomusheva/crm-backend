package kg.ortcrm.entity;

import jakarta.persistence.*;
import kg.ortcrm.entity.enums.ReferralStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "referrals", uniqueConstraints = @UniqueConstraint(columnNames = {"referred_student_id"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Referral {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "referrer_student_id", nullable = false)
    private Student referrerStudent;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "referred_student_id", nullable = false)
    private Student referredStudent;

    @Column(name = "reward_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal rewardAmount;

    @Column(name = "remaining_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal remainingAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private ReferralStatus status = ReferralStatus.REGISTERED;

    @Column(name = "available_at")
    private LocalDateTime availableAt;

    @Column(name = "applied_at")
    private LocalDateTime appliedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
