package study.konditer.forum.service.impl;

import java.util.List;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import study.konditer.forum.repository.UserRepository;

public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByName(username)
            .map(u -> new User(
                u.getName(),
                u.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_" + u.getRole().getName().name()))
            )).orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}
