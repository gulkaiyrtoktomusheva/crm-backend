package kg.ortcrm.service;

import kg.ortcrm.dto.studentcourse.StudentCourseRequest;
import kg.ortcrm.dto.studentcourse.StudentCourseResponse;
import kg.ortcrm.entity.Course;
import kg.ortcrm.entity.Student;
import kg.ortcrm.entity.StudentCourse;
import kg.ortcrm.entity.enums.StudentCourseStatus;
import kg.ortcrm.repository.CourseRepository;
import kg.ortcrm.repository.PaymentAgreementRepository;
import kg.ortcrm.repository.StudentCourseRepository;
import kg.ortcrm.repository.StudentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StudentCourseServiceTest {

    @Mock private StudentCourseRepository studentCourseRepository;
    @Mock private StudentRepository studentRepository;
    @Mock private CourseRepository courseRepository;
    @Mock private PaymentAgreementService paymentAgreementService;
    @Mock private PaymentAgreementRepository paymentAgreementRepository;
    @Mock private ReferralService referralService;

    @InjectMocks private StudentCourseService studentCourseService;

    @Test
    void createShouldApplyAvailableReferralDiscount() {
        Student student = Student.builder().id(1L).fullName("Referrer").build();
        Course course = Course.builder()
                .id(2L)
                .name("ORT")
                .price(new BigDecimal("5000.00"))
                .startDate(LocalDate.of(2026, 5, 1))
                .endDate(LocalDate.of(2026, 8, 31))
                .build();

        when(studentCourseRepository.findByStudentIdOrderByCreatedAtDesc(1L)).thenReturn(List.of());
        when(studentCourseRepository.countByStudentId(1L)).thenReturn(1L);
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(courseRepository.findById(2L)).thenReturn(Optional.of(course));
        when(referralService.getAvailableDiscountAmount(1L)).thenReturn(new BigDecimal("1000.00"));
        when(studentCourseRepository.save(any(StudentCourse.class))).thenAnswer(invocation -> {
            StudentCourse studentCourse = invocation.getArgument(0);
            studentCourse.setId(10L);
            return studentCourse;
        });

        StudentCourseResponse response = studentCourseService.create(StudentCourseRequest.builder()
                .studentId(1L)
                .courseId(2L)
                .discountAmount(new BigDecimal("500.00"))
                .status(StudentCourseStatus.ACTIVE)
                .build());

        assertEquals(new BigDecimal("1500.00"), response.getDiscountAmount());
        assertEquals(new BigDecimal("1000.00"), response.getReferralDiscountAmount());
        assertEquals(new BigDecimal("3500.00"), response.getFinalPrice());
        verify(referralService).consumeAvailableDiscount(1L, new BigDecimal("1000.00"));
        verify(paymentAgreementService).create(any());
        verify(referralService, never()).activateReferralRewardForReferredStudent(1L);
    }
}
