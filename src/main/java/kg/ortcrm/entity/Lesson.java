package kg.ortcrm.entity;

import jakarta.persistence.*;
import kg.ortcrm.entity.enums.LessonStatus;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "lessons",
        uniqueConstraints = @UniqueConstraint(columnNames = {"course_subject_id", "lesson_date"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Lesson {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_subject_id", nullable = false)
    private CourseSubject courseSubject;

    @Column(name = "lesson_date", nullable = false)
    private LocalDate lessonDate;

    private String topic;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private LessonStatus status = LessonStatus.SCHEDULED;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
