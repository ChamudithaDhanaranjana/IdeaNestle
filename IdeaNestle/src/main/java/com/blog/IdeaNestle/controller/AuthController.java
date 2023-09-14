package com.blog.IdeaNestle.controller;

import com.blog.IdeaNestle.model.Role;
import com.blog.IdeaNestle.model.User;
import com.blog.IdeaNestle.payload.request.LoginRequest;
import com.blog.IdeaNestle.payload.request.SignupRequest;
import com.blog.IdeaNestle.payload.request.UserStateUpdateRequest;
import com.blog.IdeaNestle.payload.request.UserUpdateRequest;
import com.blog.IdeaNestle.payload.response.PostResponse;
import com.blog.IdeaNestle.repository.RoleRepository;
import com.blog.IdeaNestle.repository.UserRepository;
import com.blog.IdeaNestle.security.jwt.JwtUtils;
import com.blog.IdeaNestle.service.User.AuthService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {
	private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

	@Autowired
	private AuthenticationManager authenticationManager;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private RoleRepository roleRepository;
	@Autowired
	private JwtUtils jwtUtils;
	@Autowired
	private PasswordEncoder passwordEncoder;
	@Autowired
	private AuthService authService;


	@PostMapping("/signin")
	public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
		return authService.authenticateUser(loginRequest);
	}

	@PostMapping("/signup")
	public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
		return authService.registerUser(signUpRequest);
	}


	@PostMapping("/delete/user/{username}")
	@PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
	public ResponseEntity<String> deleteUserByUsername(@PathVariable String username) {
		return authService.deleteUserByUsername(username);
	}
	@PostMapping("/active/user/{username}")
	@PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
	public ResponseEntity<String> activeUserByUsername(@PathVariable String username) {
		return authService.activeUserByUsername(username);
	}

	@GetMapping("/users/active")
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public ResponseEntity<List<User>> getActiveUsers() {
		List<User> activeUsers = userRepository.findByState(User.UserState.ACTIVE);
		return ResponseEntity.ok(activeUsers);
	}

	@GetMapping("/users/inactive")
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public ResponseEntity<List<User>> getInactiveUsers() {
		List<User> inactiveUsers = userRepository.findByState(User.UserState.INACTIVE);
		return ResponseEntity.ok(inactiveUsers);
	}

	@PostMapping("/update")
	@PreAuthorize("hasRole('ROLE_USER')")
	public ResponseEntity<String> updateUser(@Valid @RequestBody UserUpdateRequest updateRequest) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String currentUsername = auth.getName();
		return authService.updateCurrentUser(currentUsername, updateRequest);
	}

	@PutMapping("/{id}/admin")
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public ResponseEntity<String> updateAdminUserState(@PathVariable Long id, @RequestBody UserStateUpdateRequest stateUpdateRequest) {
		return authService.updateAdminUserState(id, stateUpdateRequest);
	}

	@GetMapping("/profile")
	@PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
	public ResponseEntity<User> getUserProfile(@AuthenticationPrincipal UserDetails userDetails) {
		// Get the username from the authenticated user
		String username = userDetails.getUsername();

		// Fetch the user details from the database using the username
		User user = authService.findByUsername(username);

		if (user == null) {
			// User not found in the database
			return ResponseEntity.notFound().build();
		}

		// Return the user profile data
		return ResponseEntity.ok(user);
	}

	@GetMapping("/{userId}/role")
	public ResponseEntity<List<Role.ERole>> getUserRoleNamesById(@PathVariable Long userId) {
		List<Role.ERole> roleNames = authService.getUserRoleNamesById(userId);
		return ResponseEntity.ok(roleNames);
	}

	@GetMapping("/count")
	public Long getUserCount() {
		return authService.getUserCount();
	}

	@GetMapping("/active-count")
	public long getActiveUserCount() {
		return authService.getCountOfActiveUsers();
	}

	@GetMapping("/all")
	public List<User> getAllUsersWithUserRole(@RequestParam(defaultValue = "0") int page) {
		return authService.getAllUsersWithUserRole();
	}


}

