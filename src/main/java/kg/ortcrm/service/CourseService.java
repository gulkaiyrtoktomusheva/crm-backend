package kg.ortcrm.service;

import kg.ortcrm.dto.course.CourseRequest;
import kg.ortcrm.dto.course.CourseResponse;
import kg.ortcrm.entity.Course;
import kg.ortcrm.entity.enums.CourseStatus;
import kg.ortcrm.exception.ResourceNotFoundException;
import kg.ortcrm.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;

    public List<CourseResponse> findAll() {
        return courseRepository.findAll().stream().map(this::toResponse).toList();
    }

    public CourseResponse findById(Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + id));
        return toResponse(course);
    }

    @Transactional
    public CourseResponse create(CourseRequest request) {
        validate(request);
        Course course = Course.builder()
                .name(request.getName())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .price(request.getPrice())
                .status(request.getStatus() != null ? request.getStatus() : CourseStatus.PLANNED)
                .build();
        return toResponse(courseRepository.save(course));
    }

    @Transactional
    public CourseResponse update(Long id, CourseRequest request) {
        validate(request);
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + id));
        course.setName(request.getName());
        course.setStartDate(request.getStartDate());
        course.setEndDate(request.getEndDate());
        course.setPrice(request.getPrice());
        if (request.getStatus() != null) {
            course.setStatus(request.getStatus());
        }
        return toResponse(courseRepository.save(course));
    }

    @Transactional
    public void delete(Long id) {
        if (!courseRepository.existsById(id)) {
            throw new ResourceNotFoundException("Course not found with id: " + id);
        }
        courseRepository.deleteById(id);
    }

    private void validate(CourseRequest request) {
        if (request.getEndDate().isBefore(request.getStartDate())) {
            throw new IllegalArgumentException("Course end date cannot be before start date");
        }
    }

    private CourseResponse toResponse(Course course) {
        return CourseResponse.builder()
                .id(course.getId())
                .name(course.getName())
                .startDate(course.getStartDate())
                .endDate(course.getEndDate())
                .price(course.getPrice())
                .status(course.getStatus())
                .createdAt(course.getCreatedAt())
                .build();
    }
}
