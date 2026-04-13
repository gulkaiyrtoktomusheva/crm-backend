package kg.ortcrm.controller;

import kg.ortcrm.dto.user.UserShortResponse;
import kg.ortcrm.entity.User;
import kg.ortcrm.entity.enums.Role;
import kg.ortcrm.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserRepository userRepository;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public List<UserShortResponse> getUsers(@RequestParam(required = false) Role role) {
        List<User> users = (role == null)
                ? userRepository.findAll()
                : userRepository.findAllByRole(role);

        return users.stream()
                .map(u -> new UserShortResponse(u.getId(), u.getFullName(), u.getEmail(), u.getRole()))
                .toList();
    }
}
