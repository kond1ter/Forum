package study.konditer.forum.service.impl;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import study.konditer.forum.dto.UserInputDto;
import study.konditer.forum.dto.UserOutputDto;
import study.konditer.forum.exception.NotFoundServiceException;
import study.konditer.forum.exception.ServiceException;
import study.konditer.forum.model.Role;
import study.konditer.forum.model.User;
import study.konditer.forum.model.emun.UserRoles;
import study.konditer.forum.repository.RoleRepository;
import study.konditer.forum.repository.UserRepository;
import study.konditer.forum.service.UserService;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void add(UserInputDto userDto) {
        Optional<User> user = userRepository.findByName(userDto.name());

        if (user.isPresent()) {
            throw new ServiceException("User with such username already exists");
        }
        if (!userDto.password().equals(userDto.confirmPassword())) {
            throw new ServiceException("Password fields not matching");
        }

        userRepository.save(mapToEntity(userDto));
    }

    @Override
    public UserOutputDto get(long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundServiceException("User not found"));
        return mapToDto(user);
    }

    @Override
    public UserOutputDto getByName(String name) {
        User user = userRepository.findByName(name)
                .orElseThrow(() -> new NotFoundServiceException("User not found"));
        return mapToDto(user);
    }

    @Override
    public List<UserOutputDto> getAll() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(user -> mapToDto(user)).toList();
    }

    private User mapToEntity(UserInputDto userDto) {

        Role role = roleRepository.findRoleByName(UserRoles.USER)
                .orElseThrow(() -> new NotFoundServiceException("Role not found"));

        return new User(
            userDto.name(),
            passwordEncoder.encode(userDto.password()),
            role,
            LocalDateTime.now(),
            new HashSet<>(),
            new HashSet<>(),
            new HashSet<>(),
            0,
            false
        );
    }

    private UserOutputDto mapToDto(User user) {
        return new UserOutputDto(
            user.getId(),
            user.getCreatedAt(),
            user.getName(),
            user.getApprovedReportsAmount()
        );
    }
}
