package kg.ortcrm.config;

import jakarta.annotation.PostConstruct;
import kg.ortcrm.entity.Role;
import kg.ortcrm.entity.enums.Permission;
import kg.ortcrm.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
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
    private final JdbcTemplate jdbcTemplate;
    private final DataSource dataSource;

    @PostConstruct
    @Transactional
    public void initialize() {
        createSystemRoleIfMissing("ADMIN", EnumSet.allOf(Permission.class));
        createSystemRoleIfMissing("MANAGER", EnumSet.of(
                Permission.DASHBOARD_VIEW,
                Permission.USER_VIEW,
                Permission.SUBJECT_VIEW,
                Permission.SUBJECT_CREATE,
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
                Permission.GROUP_VIEW,
                Permission.STUDENT_VIEW,
                Permission.ATTENDANCE_VIEW,
                Permission.ATTENDANCE_MARK,
                Permission.MOCK_EXAM_VIEW,
                Permission.MOCK_EXAM_CREATE,
                Permission.MOCK_EXAM_UPDATE,
                Permission.MOCK_EXAM_SCORE_MANAGE
        ));

        migrateLegacyUserRoles();
    }

    private void createSystemRoleIfMissing(String name, Set<Permission> permissions) {
        if (roleRepository.existsByNameIgnoreCase(name)) {
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
