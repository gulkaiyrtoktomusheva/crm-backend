package kg.ortcrm.dto.role;

import kg.ortcrm.entity.enums.Permission;
import lombok.Builder;

import java.util.Set;

@Builder
public record RoleResponse(Long id, String name, Set<Permission> permissions) {
}
