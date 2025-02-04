package com.cargomate.security.controller;

import com.cargomate.security.config.TokenProvider;
import com.cargomate.security.request.CreateCustomerRequest;
import com.cargomate.security.request.LoginUserRequest;
import com.cargomate.security.response.LoggedUserResponse;
import com.cargomate.security.service.UserService;
import com.cargomate.system.model.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final AuthenticationManager authenticationManager;
    private final TokenProvider jwtTokenUtil;
    private final UserService userService;

    @Autowired
    public UserController(AuthenticationManager authenticationManager, TokenProvider jwtTokenUtil, UserService userService) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenUtil = jwtTokenUtil;
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> generateToken(@Valid @RequestBody LoginUserRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String token = jwtTokenUtil.generateToken(authentication);

            LoggedUserResponse response = new LoggedUserResponse();
            response.setToken(token);
            response.setUser(userService.curentUser());

            return ResponseEntity.ok().body(response);
        } catch (AuthenticationException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid credentials", "message", "Incorrect username or password"));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<Customer> createCustomer(@Valid @RequestBody CreateCustomerRequest request) {
        Customer customer = userService.saveCustomer(request);
        return ResponseEntity.ok().body(customer);
    }

    @GetMapping("/public")
    public ResponseEntity<String> publicEndpoint() {
        return ResponseEntity.ok().body("This is a public API endpoint. No authentication required.");
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/protected")
    public ResponseEntity<String> protectedEndpoint() {
        return ResponseEntity.ok().body("This is a protected API endpoint. Only authenticated users can access.");
    }

    @PreAuthorize("hasRole('DISPATCHER')")
    @GetMapping("/dispatcher")
    public ResponseEntity<String> adminEndpoint() {
        return ResponseEntity.ok().body("This is an dispatcher-only API endpoint. Only users with DISPATCHER role can access.");
    }
}
