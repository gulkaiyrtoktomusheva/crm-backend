package kg.ortcrm.controller;

import kg.ortcrm.dto.user.UserShortResponse;
import kg.ortcrm.entity.User;
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
    @PreAuthorize("hasAuthority('USER_VIEW')")
    public List<UserShortResponse> getUsers(@RequestParam(required = false) String roleName) {
        List<User> users = (roleName == null || roleName.isBlank())
                ? userRepository.findAll()
                : userRepository.findAllByRole_NameIgnoreCase(roleName);

        return users.stream()
                .map(u -> new UserShortResponse(u.getId(), u.getFullName(), u.getEmail(), u.getRole().getName()))
                .toList();
    }
}
