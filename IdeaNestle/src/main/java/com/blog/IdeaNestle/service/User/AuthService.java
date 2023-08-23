package com.blog.IdeaNestle.service.User;

import com.blog.IdeaNestle.controller.AuthController;
import com.blog.IdeaNestle.model.Role;
import com.blog.IdeaNestle.model.User;
import com.blog.IdeaNestle.payload.request.LoginRequest;
import com.blog.IdeaNestle.payload.request.SignupRequest;
import com.blog.IdeaNestle.payload.request.UserStateUpdateRequest;
import com.blog.IdeaNestle.payload.request.UserUpdateRequest;
import com.blog.IdeaNestle.payload.response.JwtResponse;
import com.blog.IdeaNestle.payload.response.MessageResponse;
import com.blog.IdeaNestle.repository.RoleRepository;
import com.blog.IdeaNestle.repository.UserRepository;
import com.blog.IdeaNestle.security.jwt.JwtUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;



@Service
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    @Autowired
    PasswordEncoder encoder;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private SequenceGeneratorService sequenceGeneratorService;
    @Autowired
    private SequenceGeneratorService service;

    public ResponseEntity<?> authenticateUser(LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

            SecurityContextHolder.getContext().setAuthentication(authentication);

            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

            User user = userDetails.getUser();
            if (user.getState() == User.UserState.INACTIVE) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new MessageResponse("User is inactive and cannot sign in"));
            }

            String jwt = jwtUtils.generateJwtToken(authentication);

            List<String> roles = userDetails.getAuthorities().stream()
                    .map(item -> item.getAuthority())
                    .collect(Collectors.toList());

            return ResponseEntity.ok(new JwtResponse(jwt,
                    userDetails.getId(),
                    userDetails.getUsername(),
                    userDetails.getEmail(),
                    roles));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new MessageResponse("Invalid username or password"));
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new MessageResponse("User not found"));
        }
    }
    private String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }

    public ResponseEntity<?> registerUser(SignupRequest signUpRequest) {
        logger.info("The /signup endpoint has been reached");
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            logger.error("Username is already taken");
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Username is already taken!"));
        }


        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            logger.error("Email is already in use");
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Email is already in use!"));
        }

        // Create new user's account
        User user = new User();
        user.setFirstName(signUpRequest.getFirstName());
        user.setLastName(signUpRequest.getLastName());
        user.setGender(signUpRequest.getGender());
        user.setDob(signUpRequest.getDob());
        user.setUsername(signUpRequest.getUsername());
        user.setEmail(signUpRequest.getEmail());
        user.setPassword(encoder.encode(signUpRequest.getPassword()));
        user.setState(User.UserState.ACTIVE);

        logger.info("Password has been encoded");

        Set<String> strRoles = signUpRequest.getRoles();
        Set<Role> roles = new HashSet<>();

        if (strRoles == null) {
            Role userRole = roleRepository.findByName(Role.ERole.ROLE_USER)
                    .orElseThrow(() -> {
                        logger.error("Role is not found");
                        return new RuntimeException("Error: Role is not found.");
                    });
            logger.info("The user has been assigned the role of ROLE_USER");
            roles.add(userRole);

        } else {
            strRoles.forEach(role -> {
                switch (role) {
                    case "admin":
                        Role adminRole = roleRepository.findByName(Role.ERole.ROLE_ADMIN)
                                .orElseThrow(() -> {
                                    logger.error("Role is not found");
                                    return new RuntimeException("Error: Role is not found.");
                                });
                        logger.info("The user has been assigned the role of ADMIN");
                        roles.add(adminRole);

                        break;
                    default:
                        Role userRole = roleRepository.findByName(Role.ERole.ROLE_USER)
                                .orElseThrow(() -> {
                                    logger.error("Role is not found");
                                    return new RuntimeException("Error: Role is not found.");
                                });
                        logger.info("The user has been assigned the role of ROLE_USER");
                        roles.add(userRole);
                }
            });
        }

        user.setId(service.getSequenceNumber(User.SEQUENCE_NAME));
        user.setRoles(roles);
        logger.info("All roles have been assigned to the user");
        userRepository.save(user);
        logger.info("User successfully saved to the database");
        logger.info("User registered successfully!");

        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }
    public ResponseEntity<String> deleteUserByUsername(@PathVariable String username) {
        User user = userRepository.findByUsername(username);

        if (user != null) {
            // Change the user's state to INACTIVE and save
            user.setState(User.UserState.INACTIVE);
            userRepository.save(user);

            return ResponseEntity.ok("User deleted successfully");
        } else {
            return ResponseEntity.notFound().build(); // Return 404 Not Found if user not found
        }
    }

    public ResponseEntity<List<User>> getActiveUsers() {
        List<User> activeUsers = userRepository.findByState(User.UserState.ACTIVE);
        return ResponseEntity.ok(activeUsers);
    }

    public ResponseEntity<List<User>> getInactiveUsers() {
        List<User> inactiveUsers = userRepository.findByState(User.UserState.INACTIVE);
        return ResponseEntity.ok(inactiveUsers);
    }

    public ResponseEntity<String> updateCurrentUser(String currentUsername, UserUpdateRequest updateRequest) {
        Optional<User> optionalUser = Optional.ofNullable(userRepository.findByUsername(currentUsername));
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();

            // Check if the current user matches the user being updated
            if (!currentUsername.equals(updateRequest.getUsername())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("You are not authorized to update this user's account");
            }

            // Update username, password, and email
            if (updateRequest.getUsername() != null) {
                user.setUsername(updateRequest.getUsername());
            }
            if (updateRequest.getPassword() != null) {
                user.setPassword(passwordEncoder.encode(updateRequest.getPassword()));
            }
            if (updateRequest.getEmail() != null) {
                user.setEmail(updateRequest.getEmail());
            }

            userRepository.save(user);
            return ResponseEntity.ok("User updated successfully");
        } else {
            return ResponseEntity.notFound().build(); // Return 404 Not Found if user not found
        }
    }

    public ResponseEntity<String> updateAdminUserState(Long id, UserStateUpdateRequest stateUpdateRequest) {
        Optional<User> optionalUser = userRepository.findById(id);

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();

            if (stateUpdateRequest.getState() == null) {
                return ResponseEntity.badRequest().body("State field is required for admin update");
            }

            user.setState(stateUpdateRequest.getState());
            userRepository.save(user);
            return ResponseEntity.ok("User state updated successfully by admin");
        } else {
            return ResponseEntity.notFound().build();
        }
    }

}
