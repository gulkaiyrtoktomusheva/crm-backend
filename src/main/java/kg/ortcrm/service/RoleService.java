package kg.ortcrm.service;

import kg.ortcrm.dto.role.RoleRequest;
import kg.ortcrm.dto.role.RoleResponse;
import kg.ortcrm.entity.Role;
import kg.ortcrm.exception.ResourceNotFoundException;
import kg.ortcrm.repository.RoleRepository;
import kg.ortcrm.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;
import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;

    public List<RoleResponse> findAll() {
        return roleRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    public RoleResponse findById(Long id) {
        return toResponse(getRole(id));
    }

    @Transactional
    public RoleResponse create(RoleRequest request) {
        String normalizedName = normalizeName(request.getName());
        if (roleRepository.existsByNameIgnoreCase(normalizedName)) {
            throw new IllegalArgumentException("Role already exists: " + normalizedName);
        }

        Role role = Role.builder()
                .name(normalizedName)
                .permissions(new HashSet<>(request.getPermissions()))
                .build();

        return toResponse(roleRepository.save(role));
    }

    @Transactional
    public RoleResponse update(Long id, RoleRequest request) {
        Role role = getRole(id);
        String normalizedName = normalizeName(request.getName());

        roleRepository.findByNameIgnoreCase(normalizedName)
                .filter(existing -> !existing.getId().equals(id))
                .ifPresent(existing -> {
                    throw new IllegalArgumentException("Role already exists: " + normalizedName);
                });

        role.setName(normalizedName);
        role.setPermissions(new HashSet<>(request.getPermissions()));

        return toResponse(roleRepository.save(role));
    }

    @Transactional
    public void delete(Long id) {
        if (userRepository.countByRoleId(id) > 0) {
            throw new IllegalStateException("Cannot delete role assigned to users");
        }
        roleRepository.delete(getRole(id));
    }

    public Role getByName(String roleName) {
        return roleRepository.findByNameIgnoreCase(normalizeName(roleName))
                .orElseThrow(() -> new ResourceNotFoundException("Role not found: " + roleName));
    }

    private Role getRole(Long id) {
        return roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found: " + id));
    }

    private RoleResponse toResponse(Role role) {
        return RoleResponse.builder()
                .id(role.getId())
                .name(role.getName())
                .permissions(role.getPermissions())
                .build();
    }

    private String normalizeName(String roleName) {
        return roleName.trim().toUpperCase(Locale.ROOT);
    }
}
