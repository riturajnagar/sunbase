package com.rj.sunbase.Security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
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
    @CrossOrigin(origins = "*")
    @GetMapping("/verify-sync")
    public ResponseEntity<?> verifySyncAccess(@RequestHeader("Authorization") String token) {
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
    @CrossOrigin(origins = "*")
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
    @CrossOrigin(origins = "*")
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody AuthRequest authRequest) {

        User user = new User();
        user.setUsername(authRequest.getUsername());
        user.setPassword(passwordEncoder.encode(authRequest.getPassword()));
        userService.registerUser(user);
        return ResponseEntity.ok("User registered successfully");


    }

//		@CrossOrigin(origins = "http://localhost:7080")
//		@PostMapping("/proxy-auth")
//	    public ResponseEntity<?> proxyAuth(@RequestBody AuthRequest authRequest) {
//	        String url = "https://qa.sunbasedata.com/sunbase/portal/api/assignment_auth.jsp";
//
//	        // Create an HttpClient to send the request
//	        RestTemplate restTemplate = new RestTemplate();
//
//	        // Set up the request headers
//	        HttpHeaders headers = new HttpHeaders();
//	        headers.setContentType(MediaType.APPLICATION_JSON);
//
//	        // Set up the request body
//	        HttpEntity<AuthRequest> requestEntity = new HttpEntity<>(authRequest, headers);
//
//	        // Send the request and get the response
//	        ResponseEntity<String> responseEntity = restTemplate.exchange(
//	            url,
//	            HttpMethod.POST,
//	            requestEntity,
//	            String.class
//	        );
//
//	        // Return the response to the client
//	        return ResponseEntity.status(responseEntity.getStatusCode()).body(responseEntity.getBody());
//	    }

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