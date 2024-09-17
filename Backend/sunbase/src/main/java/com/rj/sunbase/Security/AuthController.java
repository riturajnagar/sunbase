package com.rj.sunbase.Security;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/api")
@Slf4j
public class AuthController {

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private UserDetailsService userDetailsService;

	@Autowired
	private JwtUtil jwtUtil;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private UserService userService;

	/**
	 * Verify if the user has access to the sync functionality.
	 *
	 * @param token The JWT token sent in the Authorization header.
	 * @return ResponseEntity indicating if the user is allowed to access the sync functionality.
	 */
	@CrossOrigin(origins = "http://localhost:8088")
	@GetMapping("/verify-sync")
	public ResponseEntity<?> verifySyncAccess(@RequestHeader("Authorization") String token) {
		log.info("Verifying token sent in the Authorization header "+ token);
		String username = jwtUtil.extractUsername(token.substring(7));
		UserDetails userDetails = userDetailsService.loadUserByUsername(username);

		// Check if the user is the allowed user
		if (username.equals("test@sunbasedata.com")) {
			return ResponseEntity.ok("User is allowed");
		} else {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body("User is not allowed");
		}
	}

	/**
	 * Authenticate a user and generate a JWT token.
	 *
	 * @param authRequest The authentication request containing username and password.
	 * @return ResponseEntity containing the JWT token if authentication is successful.
	 */
	@CrossOrigin(origins = "http://localhost:8088")
	@PostMapping("/auth")
	public ResponseEntity<?> authenticate(@RequestBody AuthRequest authRequest) {

		try {
			authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));
		} catch (Exception e) {
			return ResponseEntity.status(401).body("Bad credentials");
		}
		final UserDetails userDetails = userDetailsService.loadUserByUsername(authRequest.getUsername());
		final String jwt = jwtUtil.generateToken(userDetails);
		return ResponseEntity.ok(new AuthResponse(jwt));
	}

	/**
	 * Register a new user by encoding the password and saving the user details.
	 *
	 * @param authRequest The registration request containing username and password.
	 * @return ResponseEntity indicating successful registration.
	 */
	@CrossOrigin(origins = "http://localhost:8088")
	@PostMapping("/register")
	public ResponseEntity<?> register(@RequestBody AuthRequest authRequest) {

		User user = new User();
		user.setUsername(authRequest.getUsername());
		user.setPassword(passwordEncoder.encode(authRequest.getPassword()));
		userService.registerUser(user);
		return ResponseEntity.ok("User registered successfully");


	}

	@CrossOrigin(origins = "http://localhost:8088")
	@GetMapping("/proxy-auth")
	public ResponseEntity<?> proxyAuth() {
		final String STATIC_LOGIN_ID = "test@sunbasedata.com";
		final String STATIC_PASSWORD = "Test@123";
		String url = "https://qa.sunbasedata.com/sunbase/portal/api/assignment_auth.jsp";

		RestTemplate restTemplate = new RestTemplate();

		String jsonPayload = String.format("{\"login_id\":\"%s\",\"password\":\"%s\"}",
				STATIC_LOGIN_ID, STATIC_PASSWORD);

		HttpHeaders headers = new HttpHeaders();
		headers.set("Content-Type", "application/json");

		HttpEntity<?> requestEntity = new HttpEntity<>(jsonPayload, headers);

		log.info("ENTITY " + requestEntity.toString());

		ResponseEntity<String> responseEntity = restTemplate.exchange(
				url,
				HttpMethod.POST,
				requestEntity,
				String.class
		);

		log.info("RESULT " + responseEntity.getBody().toString());

		String token = extractAccessToken(responseEntity.getBody());

		log.info("Extracted Token: " + token);

		return ResponseEntity.status(responseEntity.getStatusCode()).body(responseEntity.getBody());
	}

	private String extractAccessToken(String responseBody){
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			JsonNode rootNode = objectMapper.readTree(responseBody);
			return rootNode.path("access_token").asText();
		}
		catch (Exception e) {
			log.error("Error parsing JSON: {}", e.getMessage());
			throw new RuntimeException("Error parsing", e);
		}
	}

	/**
	 * Data transfer object for authentication request.
	 */
	public static class AuthRequest {
		private String username;
		private String password;


		public String getUsername() {
			return username;
		}

		public void setUsername(String username) {
			this.username = username;
		}

		public String getPassword() {
			return password;
		}

		public void setPassword(String password) {
			this.password = password;
		}
	}

	public static class AuthResponse {
		private String token;

		public AuthResponse(String token) {
			this.token = token;
		}


		public String getToken() {
			return token;
		}
	}

}