package kg.ortcrm.service;

import kg.ortcrm.entity.PaymentSchedule;
import kg.ortcrm.entity.enums.PaymentScheduleStatus;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;

@Component
public class PaymentScheduleStatusResolver {

    public PaymentScheduleStatus resolve(PaymentSchedule schedule) {
        BigDecimal paidAmount = amountOrZero(schedule.getPaidAmount());
        BigDecimal amountDue = amountOrZero(schedule.getAmountDue());

        if (paidAmount.compareTo(amountDue) >= 0) {
            return PaymentScheduleStatus.PAID;
        }
        if (schedule.getDueDate() != null && schedule.getDueDate().isBefore(LocalDate.now())) {
            return PaymentScheduleStatus.OVERDUE;
        }
        if (paidAmount.compareTo(BigDecimal.ZERO) > 0) {
            return PaymentScheduleStatus.PARTIAL;
        }
        return PaymentScheduleStatus.PENDING;
    }

    public BigDecimal remainingAmount(PaymentSchedule schedule) {
        return amountOrZero(schedule.getAmountDue())
                .subtract(amountOrZero(schedule.getPaidAmount()))
                .max(BigDecimal.ZERO);
    }

    private BigDecimal amountOrZero(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }
}
