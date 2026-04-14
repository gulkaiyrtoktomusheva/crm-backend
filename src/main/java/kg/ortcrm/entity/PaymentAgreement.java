package kg.ortcrm.entity;

import jakarta.persistence.*;
import kg.ortcrm.entity.enums.PaymentAgreementStatus;
import kg.ortcrm.entity.enums.PaymentAgreementType;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "payment_agreements")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentAgreement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_course_id", nullable = false)
    private StudentCourse studentCourse;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentAgreementType type;

    @Column(name = "total_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "first_due_date", nullable = false)
    private LocalDate firstDueDate;

    @Column(name = "months_count", nullable = false)
    private Integer monthsCount;

    @Column(name = "billing_day")
    private Integer billingDay;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private PaymentAgreementStatus status = PaymentAgreementStatus.ACTIVE;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
