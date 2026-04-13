package kg.ortcrm.dto.user;

import kg.ortcrm.entity.enums.Role;

public record UserShortResponse(Long id, String fullName, String email, Role role) {
}
