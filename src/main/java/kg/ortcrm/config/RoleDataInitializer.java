package kg.ortcrm.config;

import kg.ortcrm.entity.Role;
import kg.ortcrm.entity.User;
import kg.ortcrm.entity.enums.Permission;
import kg.ortcrm.repository.RoleRepository;
import kg.ortcrm.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class RoleDataInitializer {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JdbcTemplate jdbcTemplate;
    private final DataSource dataSource;

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void initialize() {
        createSystemRoleIfMissing("ADMIN", EnumSet.allOf(Permission.class));
        createSystemRoleIfMissing("MANAGER", EnumSet.of(
                Permission.DASHBOARD_VIEW,
                Permission.USER_VIEW,
                Permission.USER_UPDATE,
                Permission.USER_DELETE,
                Permission.SUBJECT_VIEW,
                Permission.SUBJECT_CREATE,
                Permission.COURSE_VIEW,
                Permission.COURSE_CREATE,
                Permission.COURSE_UPDATE,
                Permission.COURSE_MANAGE_SUBJECTS,
                Permission.GROUP_VIEW,
                Permission.GROUP_CREATE,
                Permission.GROUP_UPDATE,
                Permission.GROUP_MANAGE_STUDENTS,
                Permission.STUDENT_VIEW,
                Permission.STUDENT_CREATE,
                Permission.STUDENT_UPDATE,
                Permission.LEAD_VIEW,
                Permission.LEAD_CREATE,
                Permission.LEAD_UPDATE,
                Permission.PAYMENT_VIEW,
                Permission.PAYMENT_CREATE,
                Permission.PAYMENT_UPDATE,
                Permission.ATTENDANCE_VIEW,
                Permission.ATTENDANCE_MARK,
                Permission.MOCK_EXAM_VIEW,
                Permission.MOCK_EXAM_CREATE,
                Permission.MOCK_EXAM_UPDATE,
                Permission.MOCK_EXAM_SCORE_MANAGE
        ));
        createSystemRoleIfMissing("TEACHER", EnumSet.of(
                Permission.DASHBOARD_VIEW,
                Permission.SUBJECT_VIEW,
                Permission.COURSE_VIEW,
                Permission.GROUP_VIEW,
                Permission.STUDENT_VIEW,
                Permission.ATTENDANCE_VIEW,
                Permission.ATTENDANCE_MARK,
                Permission.MOCK_EXAM_VIEW,
                Permission.MOCK_EXAM_CREATE,
                Permission.MOCK_EXAM_UPDATE,
                Permission.MOCK_EXAM_SCORE_MANAGE
        ));

        ensureDefaultAdminUser();
        migrateLegacyUserRoles();
    }

    private void createSystemRoleIfMissing(String name, Set<Permission> permissions) {
        java.util.Optional<Role> existingRole = roleRepository.findAll().stream()
                .filter(role -> role.getName().equalsIgnoreCase(name))
                .findFirst();
        if (existingRole.isPresent()) {
            Role role = existingRole.get();
            Set<Permission> mergedPermissions = EnumSet.copyOf(role.getPermissions().isEmpty()
                    ? permissions
                    : role.getPermissions());
            mergedPermissions.addAll(permissions);
            role.setPermissions(mergedPermissions);
            roleRepository.save(role);
            return;
        }
        roleRepository.save(Role.builder()
                .name(name)
                .permissions(EnumSet.copyOf(permissions))
                .build());
    }

    private void migrateLegacyUserRoles() {
        if (!hasColumn("users", "role")) {
            return;
        }

        Map<String, Long> roleIds = roleRepository.findAll().stream()
                .collect(Collectors.toMap(Role::getName, Role::getId));

        jdbcTemplate.query(
                "select id, role from users where role_id is null and role is not null",
                (rs) -> {
                    String legacyRole = rs.getString("role");
                    Long roleId = roleIds.get(legacyRole == null ? null : legacyRole.toUpperCase());
                    if (roleId != null) {
                        jdbcTemplate.update("update users set role_id = ? where id = ?", roleId, rs.getLong("id"));
                    }
                }
        );
    }

    private void ensureDefaultAdminUser() {
        if (userRepository.existsByEmail("admin@gmail.com")) {
            return;
        }

        Role adminRole = roleRepository.findByNameIgnoreCase("ADMIN")
                .orElseThrow(() -> new IllegalStateException("ADMIN role was not created"));

        userRepository.save(User.builder()
                .email("admin@gmail.com")
                .password(passwordEncoder.encode("admin"))
                .fullName("Administrator")
                .role(adminRole)
                .build());
    }

    private boolean hasColumn(String tableName, String columnName) {
        try (Connection connection = dataSource.getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();
            try (ResultSet resultSet = metaData.getColumns(connection.getCatalog(), null, tableName, columnName)) {
                if (resultSet.next()) {
                    return true;
                }
            }
            try (ResultSet resultSet = metaData.getColumns(connection.getCatalog(), null, tableName.toUpperCase(), columnName.toUpperCase())) {
                return resultSet.next();
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to inspect database metadata", e);
        }
    }
}
