package kg.ortcrm.service;

import kg.ortcrm.dto.studentcourse.StudentCourseRequest;
import kg.ortcrm.dto.studentcourse.StudentCourseResponse;
import kg.ortcrm.dto.paymentagreement.PaymentAgreementRequest;
import kg.ortcrm.entity.Course;
import kg.ortcrm.entity.PaymentAgreement;
import kg.ortcrm.entity.Student;
import kg.ortcrm.entity.StudentCourse;
import kg.ortcrm.entity.enums.PaymentAgreementStatus;
import kg.ortcrm.entity.enums.PaymentAgreementType;
import kg.ortcrm.entity.enums.StudentCourseStatus;
import kg.ortcrm.exception.ResourceNotFoundException;
import kg.ortcrm.repository.CourseRepository;
import kg.ortcrm.repository.PaymentAgreementRepository;
import kg.ortcrm.repository.StudentCourseRepository;
import kg.ortcrm.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StudentCourseService {

    private final StudentCourseRepository studentCourseRepository;
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;
    private final PaymentAgreementService paymentAgreementService;
    private final PaymentAgreementRepository paymentAgreementRepository;
    private final ReferralService referralService;

    public List<StudentCourseResponse> findByStudentId(Long studentId) {
        return studentCourseRepository.findByStudentIdOrderByCreatedAtDesc(studentId).stream()
                .map(this::toResponse)
                .toList();
    }

    public StudentCourseResponse findById(Long id) {
        StudentCourse studentCourse = studentCourseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student course not found with id: " + id));
        return toResponse(studentCourse);
    }

    @Transactional
    public StudentCourseResponse create(StudentCourseRequest request) {
        ensureNoDuplicateActiveEnrollment(request.getStudentId(), request.getCourseId(), null);

        Student student = studentRepository.findById(request.getStudentId())
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + request.getStudentId()));
        Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + request.getCourseId()));
        validateDiscountAgainstCourse(course, request);
        boolean firstEnrollment = studentCourseRepository.countByStudentId(student.getId()) == 0;
        BigDecimal manualDiscount = defaultDiscount(request.getDiscountAmount());
        BigDecimal availableReferralDiscount = referralService.getAvailableDiscountAmount(student.getId());
        BigDecimal referralDiscountToApply = availableReferralDiscount.min(course.getPrice().subtract(manualDiscount).max(BigDecimal.ZERO));
        BigDecimal totalDiscount = manualDiscount.add(referralDiscountToApply);

        StudentCourse studentCourse = StudentCourse.builder()
                .student(student)
                .course(course)
                .coursePrice(course.getPrice())
                .discountAmount(totalDiscount)
                .referralDiscountAmount(referralDiscountToApply)
                .finalPrice(course.getPrice().subtract(totalDiscount))
                .status(request.getStatus() != null ? request.getStatus() : StudentCourseStatus.ACTIVE)
                .build();

        StudentCourse saved = studentCourseRepository.save(studentCourse);
        if (referralDiscountToApply.compareTo(BigDecimal.ZERO) > 0) {
            referralService.consumeAvailableDiscount(student.getId(), referralDiscountToApply);
        }
        if (firstEnrollment && saved.getStatus() != StudentCourseStatus.CANCELLED) {
            referralService.activateReferralRewardForReferredStudent(student.getId());
        }
        if (saved.getStatus() != StudentCourseStatus.CANCELLED) {
            createDefaultPaymentAgreement(saved);
        }
        return toResponse(saved);
    }

    @Transactional
    public StudentCourseResponse update(Long id, StudentCourseRequest request) {
        StudentCourse studentCourse = studentCourseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student course not found with id: " + id));
        ensureNoDuplicateActiveEnrollment(request.getStudentId(), request.getCourseId(), id);

        Student student = studentRepository.findById(request.getStudentId())
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + request.getStudentId()));
        Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + request.getCourseId()));
        validateDiscountAgainstCourse(course, request);
        ensureFinancialTermsAreMutable(studentCourse, request, course);
        BigDecimal manualDiscount = defaultDiscount(request.getDiscountAmount());

        studentCourse.setStudent(student);
        studentCourse.setCourse(course);
        studentCourse.setCoursePrice(course.getPrice());
        studentCourse.setDiscountAmount(manualDiscount.add(studentCourse.getReferralDiscountAmount()));
        studentCourse.setFinalPrice(course.getPrice().subtract(manualDiscount.add(studentCourse.getReferralDiscountAmount())));
        studentCourse.setStatus(request.getStatus() != null ? request.getStatus() : studentCourse.getStatus());

        StudentCourse saved = studentCourseRepository.save(studentCourse);
        if (saved.getStatus() == StudentCourseStatus.CANCELLED) {
            paymentAgreementRepository.findByStudentCourseId(saved.getId())
                    .filter(agreement -> agreement.getStatus() == PaymentAgreementStatus.ACTIVE)
                    .ifPresent(this::cancelAgreement);
        } else if (paymentAgreementRepository.findByStudentCourseId(saved.getId()).isEmpty()) {
            createDefaultPaymentAgreement(saved);
        }
        return toResponse(saved);
    }

    @Transactional
    public void delete(Long id) {
        StudentCourse studentCourse = studentCourseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student course not found with id: " + id));
        if (paymentAgreementRepository.findByStudentCourseId(id).isPresent()) {
            throw new IllegalArgumentException("Cannot delete student course with payment history or agreement; cancel it instead");
        }
        studentCourseRepository.delete(studentCourse);
    }

    private void ensureNoDuplicateActiveEnrollment(Long studentId, Long courseId, Long currentId) {
        List<StudentCourse> activeCourses = studentCourseRepository.findByStudentIdOrderByCreatedAtDesc(studentId).stream()
                .filter(course -> course.getCourse().getId().equals(courseId))
                .filter(course -> course.getStatus() == StudentCourseStatus.ACTIVE)
                .filter(course -> currentId == null || !course.getId().equals(currentId))
                .toList();
        if (!activeCourses.isEmpty()) {
            throw new IllegalArgumentException("Student already has an active enrollment for this course");
        }
    }

    private BigDecimal defaultDiscount(BigDecimal discount) {
        return discount == null ? BigDecimal.ZERO : discount;
    }

    private void createDefaultPaymentAgreement(StudentCourse studentCourse) {
        paymentAgreementService.create(PaymentAgreementRequest.builder()
                .studentCourseId(studentCourse.getId())
                .type(PaymentAgreementType.FULL)
                .firstDueDate(studentCourse.getCourse().getStartDate())
                .monthsCount(1)
                .build());
    }

    private void cancelAgreement(PaymentAgreement agreement) {
        agreement.setStatus(PaymentAgreementStatus.CANCELLED);
        paymentAgreementRepository.save(agreement);
    }

    private void validateDiscountAgainstCourse(Course course, StudentCourseRequest request) {
        BigDecimal discount = defaultDiscount(request.getDiscountAmount());
        if (discount.compareTo(course.getPrice()) > 0) {
            throw new IllegalArgumentException("Discount cannot exceed course price");
        }
    }

    private void ensureFinancialTermsAreMutable(StudentCourse studentCourse, StudentCourseRequest request, Course course) {
        boolean financialChange = !studentCourse.getCourse().getId().equals(course.getId())
                || defaultDiscount(request.getDiscountAmount())
                .compareTo(studentCourse.getDiscountAmount().subtract(studentCourse.getReferralDiscountAmount())) != 0;
        if (financialChange && paymentAgreementRepository.existsByStudentCourseIdAndStatusIn(
                studentCourse.getId(),
                List.of(PaymentAgreementStatus.ACTIVE, PaymentAgreementStatus.COMPLETED))) {
            throw new IllegalArgumentException("Cannot change course or discount after payment agreement has been created");
        }
    }

    private StudentCourseResponse toResponse(StudentCourse studentCourse) {
        return StudentCourseResponse.builder()
                .id(studentCourse.getId())
                .studentId(studentCourse.getStudent().getId())
                .studentName(studentCourse.getStudent().getFullName())
                .courseId(studentCourse.getCourse().getId())
                .courseName(studentCourse.getCourse().getName())
                .coursePrice(studentCourse.getCoursePrice())
                .discountAmount(studentCourse.getDiscountAmount())
                .referralDiscountAmount(studentCourse.getReferralDiscountAmount())
                .finalPrice(studentCourse.getFinalPrice())
                .startDate(studentCourse.getCourse().getStartDate())
                .endDate(studentCourse.getCourse().getEndDate())
                .status(studentCourse.getStatus())
                .createdAt(studentCourse.getCreatedAt())
                .build();
    }
}
