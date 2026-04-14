package kg.ortcrm.service;

import kg.ortcrm.dto.user.UserRequest;
import kg.ortcrm.dto.user.UserResponse;
import kg.ortcrm.dto.user.UserUpdateRequest;
import kg.ortcrm.entity.Role;
import kg.ortcrm.entity.User;
import kg.ortcrm.exception.ResourceNotFoundException;
import kg.ortcrm.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;

    public List<UserResponse> findAll(String roleName) {
        List<User> users = (roleName == null || roleName.isBlank())
                ? userRepository.findAll()
                : userRepository.findAllByRole_NameIgnoreCase(roleName);
        return users.stream().map(this::toResponse).toList();
    }

    public UserResponse findById(Long id) {
        return toResponse(getUser(id));
    }

    @Transactional
    public UserResponse create(UserRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already exists: " + request.getEmail());
        }

        Role role = roleService.getByName(request.getRoleName());
        User user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(role)
                .phone(request.getPhone())
                .build();

        return toResponse(userRepository.save(user));
    }

    @Transactional
    public UserResponse update(Long id, UserUpdateRequest request) {
        User user = getUser(id);

        userRepository.findByEmail(request.getEmail())
                .filter(existing -> !existing.getId().equals(id))
                .ifPresent(existing -> {
                    throw new IllegalArgumentException("Email already exists: " + request.getEmail());
                });

        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setRole(roleService.getByName(request.getRoleName()));
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        return toResponse(userRepository.save(user));
    }

    @Transactional
    public void delete(Long id) {
        userRepository.delete(getUser(id));
    }

    private User getUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }

    private UserResponse toResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .roleName(user.getRole() != null ? user.getRole().getName() : null)
                .phone(user.getPhone())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
