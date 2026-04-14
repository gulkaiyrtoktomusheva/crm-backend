package kg.ortcrm.service;

import kg.ortcrm.dto.group.GroupRequest;
import kg.ortcrm.entity.Group;
import kg.ortcrm.entity.Role;
import kg.ortcrm.entity.Subject;
import kg.ortcrm.entity.User;
import kg.ortcrm.mapper.GroupMapper;
import kg.ortcrm.mapper.StudentMapper;
import kg.ortcrm.repository.GroupRepository;
import kg.ortcrm.repository.StudentRepository;
import kg.ortcrm.repository.SubjectRepository;
import kg.ortcrm.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GroupServiceTest {

    @Mock private GroupRepository groupRepository;
    @Mock private SubjectRepository subjectRepository;
    @Mock private UserRepository userRepository;
    @Mock private StudentRepository studentRepository;
    @Mock private GroupMapper groupMapper;
    @Mock private StudentMapper studentMapper;

    @InjectMocks private GroupService groupService;

    @Test
    void createShouldRejectInvalidDates() {
        GroupRequest request = GroupRequest.builder()
                .name("A1")
                .subjectId(1L)
                .startDate(LocalDate.of(2026, 5, 1))
                .endDate(LocalDate.of(2026, 4, 1))
                .build();

        assertThrows(IllegalArgumentException.class, () -> groupService.create(request));
    }

    @Test
    void createShouldRejectNonTeacherUser() {
        GroupRequest request = GroupRequest.builder()
                .name("A1")
                .subjectId(1L)
                .teacherId(2L)
                .build();

        when(groupMapper.toEntity(request)).thenReturn(Group.builder().name("A1").build());
        when(subjectRepository.findById(1L)).thenReturn(java.util.Optional.of(Subject.builder().id(1L).name("math").build()));
        when(userRepository.findById(2L)).thenReturn(java.util.Optional.of(User.builder()
                .id(2L)
                .role(Role.builder().id(10L).name("MANAGER").build())
                .build()));

        assertThrows(IllegalArgumentException.class, () -> groupService.create(request));
    }
}
