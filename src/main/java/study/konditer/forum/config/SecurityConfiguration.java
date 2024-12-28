package study.konditer.forum.config;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.context.DelegatingSecurityContextRepository;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.RequestAttributeSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;

import study.konditer.forum.model.emun.UserRoles;
import study.konditer.forum.repository.UserRepository;
import study.konditer.forum.service.impl.UserDetailsServiceImpl;

@Configuration
public class SecurityConfiguration {
    private final UserRepository userRepository;

    public SecurityConfiguration(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http, SecurityContextRepository securityContextRepository) throws Exception {
        
        http
            .authorizeHttpRequests(authorizeHttpRequests ->
                authorizeHttpRequests
                    .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                    .requestMatchers("/favicon.ico").permitAll()
                    .requestMatchers("/error", "/users/*").permitAll()
                    .requestMatchers("/questions", "/questions/*", "/questions/close/*").authenticated()
                    .requestMatchers("/answers/*").authenticated()
                    .requestMatchers("/reactions/*").authenticated()
                    .requestMatchers("/pins/create", "/pins/create/*").authenticated()
                    .requestMatchers("/reports/create", "/reports/create/*").authenticated()
                    .requestMatchers("/pins", "/pins/approve/*", "/pins/reject/*").hasRole(UserRoles.ADMIN.name())
                    .requestMatchers("/reports", "/reports/*", "/reports/approve/*", "/reports/reject/*").hasRole(UserRoles.ADMIN.name())
            )
            .formLogin(formLogin ->
                formLogin
                    .loginPage("/users/login")
                    .usernameParameter("username")
                    .passwordParameter("password")
                    .defaultSuccessUrl("/questions")
                    .failureForwardUrl("/users/login-error")
            )
            .logout(logout ->
                logout.logoutUrl("/users/logout")
                    .logoutSuccessUrl("/users/login")
                    .invalidateHttpSession(true)
            )
            .securityContext(
                    securityContext -> securityContext
                        .securityContextRepository(securityContextRepository)
            );
        
            return http.build();
    }

    @Bean
    public SecurityContextRepository securityContextRepository() {
        return new DelegatingSecurityContextRepository(
                new RequestAttributeSecurityContextRepository(),
                new HttpSessionSecurityContextRepository()
        );
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService() { 
        return new UserDetailsServiceImpl(userRepository); 
    }
}