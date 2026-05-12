package com.mitanshu.spring_ecomm.controllers;

import com.mitanshu.spring_ecomm.configs.security.jwt.JwtUtils;
import com.mitanshu.spring_ecomm.dtos.LoginRequest;
import com.mitanshu.spring_ecomm.dtos.LoginResponse;
import com.mitanshu.spring_ecomm.dtos.MessageResponse;
import com.mitanshu.spring_ecomm.dtos.SignupRequest;
import com.mitanshu.spring_ecomm.entities.AppRole;
import com.mitanshu.spring_ecomm.entities.Role;
import com.mitanshu.spring_ecomm.entities.User;
import com.mitanshu.spring_ecomm.repositories.RoleRepository;
import com.mitanshu.spring_ecomm.repositories.UsersRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/auth/")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/public/signin")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) throws ParseException {
        Authentication authentication;
        try {
            authentication = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
        } catch (AuthenticationException e){
            Map<String, Object> map = new HashMap<>();
            map.put("message", "Bad credentials");
            map.put("status", false);
            return new ResponseEntity<Object>(map, HttpStatus.UNAUTHORIZED);
        }

        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        assert userDetails != null;
        String token = jwtUtils.generateTokenFromUsername(userDetails);

        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();
        // Old way
        //  List<String> roles = userDetails.getAuthorities().stream()
        //        .map(item -> item.getAuthority())
        //        .collect(Collectors.toList());

        // Prepare the response body, now including the JWT token directly in the body
        LoginResponse response = new LoginResponse(userDetails.getUsername(), roles, token);

        // Return the response entity with the JWT token included in the response body
        return ResponseEntity.ok(response);
    }

    @PostMapping("/public/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signupRequest){
        if(usersRepository.existsByUsername(signupRequest.getUsername())){
            return ResponseEntity.badRequest().body("Error: Username is already taken!");
        }

        if(usersRepository.existsByEmail(signupRequest.getEmail())){
            return ResponseEntity.badRequest().body("Error: Email is already in use!");
        }

        Role userRole = roleRepository.findByRoleName(AppRole.ROLE_USER)
                .orElseGet(() -> roleRepository.save(new Role(AppRole.ROLE_USER)));

        User user = new User(signupRequest.getUsername(), signupRequest.getFullName(), signupRequest.getEmail(),
                passwordEncoder.encode(signupRequest.getPassword()), userRole);
        usersRepository.save(user);

        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }
}
