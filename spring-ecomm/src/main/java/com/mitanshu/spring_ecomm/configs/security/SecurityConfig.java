package com.mitanshu.spring_ecomm.configs.security;

import com.mitanshu.spring_ecomm.configs.security.jwt.AuthEntryPointJwt;
import com.mitanshu.spring_ecomm.configs.security.jwt.AuthTokenFilter;
import com.mitanshu.spring_ecomm.entities.AppRole;
import com.mitanshu.spring_ecomm.entities.Role;
import com.mitanshu.spring_ecomm.entities.User;
import com.mitanshu.spring_ecomm.repositories.RoleRepository;
import com.mitanshu.spring_ecomm.repositories.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Autowired
    private AuthEntryPointJwt authEntryPointJwt;

    @Bean
    public AuthTokenFilter authTokenFilter() {
        return new AuthTokenFilter();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) {
        http.authorizeHttpRequests(requests
                -> requests
//                .requestMatchers("/api/products/**").hasRole("ADMIN")
                .requestMatchers("/h2-console/**").permitAll()
                .requestMatchers("/api/auth/public/**").permitAll()
                .anyRequest().authenticated());

//      Disable CSRF & frame options to enable H2-console access
        http.csrf(AbstractHttpConfigurer::disable); // csrf -> csrf.disable()
        http.headers(header -> header.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable));

        // Stateless Session
        http.sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // configure JWT exception handling
        http.exceptionHandling(exception -> exception.authenticationEntryPoint(authEntryPointJwt));
        http.addFilterBefore(authTokenFilter(), UsernamePasswordAuthenticationFilter.class);

//        http.formLogin(Customizer.withDefaults());
//        http.httpBasic(Customizer.withDefaults());

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) {
        return authenticationConfiguration.getAuthenticationManager();
    }

//    @Bean
//    public UserDetailsService userDetailsService(){
//        InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
////        JdbcUserDetailsManager manager = new JdbcUserDetailsManager(dataSource); // need to create users table, etc. manually
//
//        if(!manager.userExists("admin1")){
//            manager.createUser(
//                    User.withUsername("admin1")
//                            .password("{noop}password")
//                            .roles("ADMIN")
//                            .build()
//            );
//        }
//
//        return manager;
//    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CommandLineRunner init(UsersRepository usersRepository, RoleRepository roleRepository,
                                  PasswordEncoder passwordEncoder) {
        return args -> {

            Role userRole = roleRepository.findByRoleName(AppRole.ROLE_USER)
                    .orElseGet(() -> roleRepository.save(new Role(AppRole.ROLE_USER)));

            Role adminRole = roleRepository.findByRoleName(AppRole.ROLE_ADMIN)
                    .orElseGet(() -> roleRepository.save(new Role(AppRole.ROLE_ADMIN)));

            if (!usersRepository.existsByUsername("user1")) {
                User user1 = new User("user1", "user1", "user1@test.com", passwordEncoder.encode("password"), userRole);
                usersRepository.save(user1);
            }
        };
    }
}
