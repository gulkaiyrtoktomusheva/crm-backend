package kg.ortcrm.service;

import kg.ortcrm.dto.attendance.AttendanceMarkRequest;
import kg.ortcrm.dto.attendance.AttendanceResponse;
import kg.ortcrm.entity.Attendance;
import kg.ortcrm.entity.Group;
import kg.ortcrm.entity.Student;
import kg.ortcrm.exception.ResourceNotFoundException;
import kg.ortcrm.mapper.AttendanceMapper;
import kg.ortcrm.repository.AttendanceRepository;
import kg.ortcrm.repository.GroupRepository;
import kg.ortcrm.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final GroupRepository groupRepository;
    private final StudentRepository studentRepository;
    private final AttendanceMapper attendanceMapper;

    public List<AttendanceResponse> findByGroupAndDate(Long groupId, LocalDate date) {
        List<Attendance> attendances = attendanceRepository.findByGroupIdAndLessonDate(groupId, date);
        return attendanceMapper.toResponseList(attendances);
    }

    public List<AttendanceResponse> findByStudent(Long studentId) {
        List<Attendance> attendances = attendanceRepository.findByStudentId(studentId);
        return attendanceMapper.toResponseList(attendances);
    }

    @Transactional
    public List<AttendanceResponse> markAttendance(Long groupId, AttendanceMarkRequest request) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Group not found with id: " + groupId));

        List<Attendance> savedAttendances = new ArrayList<>();

        for (AttendanceMarkRequest.AttendanceRecord record : request.getRecords()) {
            Student student = studentRepository.findById(record.getStudentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + record.getStudentId()));

            Optional<Attendance> existingAttendance = attendanceRepository
                    .findByStudentIdAndGroupIdAndLessonDate(record.getStudentId(), groupId, request.getLessonDate());

            Attendance attendance;
            if (existingAttendance.isPresent()) {
                attendance = existingAttendance.get();
                attendance.setPresent(record.getPresent());
            } else {
                attendance = Attendance.builder()
                        .student(student)
                        .group(group)
                        .lessonDate(request.getLessonDate())
                        .present(record.getPresent())
                        .build();
            }

            savedAttendances.add(attendanceRepository.save(attendance));
        }

        return attendanceMapper.toResponseList(savedAttendances);
    }
}
