package kg.ortcrm.service;

import kg.ortcrm.dto.payment.PaymentRequest;
import kg.ortcrm.dto.payment.PaymentResponse;
import kg.ortcrm.entity.Payment;
import kg.ortcrm.entity.Student;
import kg.ortcrm.entity.enums.PaymentStatus;
import kg.ortcrm.exception.ResourceNotFoundException;
import kg.ortcrm.mapper.PaymentMapper;
import kg.ortcrm.repository.PaymentRepository;
import kg.ortcrm.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final StudentRepository studentRepository;
    private final PaymentMapper paymentMapper;

    public Page<PaymentResponse> findAll(PaymentStatus status, Pageable pageable) {
        Page<Payment> payments;
        if (status != null) {
            payments = paymentRepository.findByStatus(status, pageable);
        } else {
            payments = paymentRepository.findAll(pageable);
        }
        return payments.map(paymentMapper::toResponse);
    }

    public List<PaymentResponse> findByStudentId(Long studentId) {
        List<Payment> payments = paymentRepository.findByStudentIdOrderByDueDateAsc(studentId);
        return paymentMapper.toResponseList(payments);
    }

    public PaymentResponse findById(Long id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with id: " + id));
        return paymentMapper.toResponse(payment);
    }

    @Transactional
    public PaymentResponse create(PaymentRequest request) {
        Payment payment = paymentMapper.toEntity(request);

        Student student = studentRepository.findById(request.getStudentId())
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + request.getStudentId()));
        payment.setStudent(student);

        if (payment.getStatus() == null) {
            payment.setStatus(PaymentStatus.PENDING);
        }

        Payment savedPayment = paymentRepository.save(payment);
        return paymentMapper.toResponse(savedPayment);
    }

    @Transactional
    public PaymentResponse update(Long id, PaymentRequest request) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with id: " + id));

        paymentMapper.updateEntity(payment, request);

        if (request.getStudentId() != null) {
            Student student = studentRepository.findById(request.getStudentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + request.getStudentId()));
            payment.setStudent(student);
        }

        Payment updatedPayment = paymentRepository.save(payment);
        return paymentMapper.toResponse(updatedPayment);
    }

    @Transactional
    public PaymentResponse updateStatus(Long id, PaymentStatus status) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with id: " + id));

        payment.setStatus(status);

        if (status == PaymentStatus.PAID && payment.getPaidDate() == null) {
            payment.setPaidDate(LocalDate.now());
        }

        Payment updatedPayment = paymentRepository.save(payment);
        return paymentMapper.toResponse(updatedPayment);
    }

    @Transactional
    public void delete(Long id) {
        if (!paymentRepository.existsById(id)) {
            throw new ResourceNotFoundException("Payment not found with id: " + id);
        }
        paymentRepository.deleteById(id);
    }

    public List<PaymentResponse> findOverduePayments() {
        List<Payment> payments = paymentRepository.findByStatusAndDueDateBefore(PaymentStatus.PENDING, LocalDate.now());
        return paymentMapper.toResponseList(payments);
    }
}
