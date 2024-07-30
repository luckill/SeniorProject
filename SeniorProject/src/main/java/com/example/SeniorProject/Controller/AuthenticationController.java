package com.example.SeniorProject.Controller;

import com.example.SeniorProject.*;
import com.example.SeniorProject.Model.*;
import com.example.SeniorProject.Service.*;
import com.example.SeniorProject.DTOs.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/auth")
@RestController
public class AuthenticationController
{
    private final JwtService jwtService;
    private final AuthenticationService authenticationService;
    private final JwtTokenBlacklistService jwtTokenBlacklistService;

    public AuthenticationController(JwtService jwtService, AuthenticationService authenticationService, JwtTokenBlacklistService jwtTokenBlacklistService)
    {
        this.jwtService = jwtService;
        this.authenticationService = authenticationService;
        this.jwtTokenBlacklistService = jwtTokenBlacklistService;
    }

    @PostMapping("/signup")
    public ResponseEntity<?> register(@RequestBody RegisterUserDTO registerUserDTO)
    {
        authenticationService.signUp(registerUserDTO);
        return ResponseEntity.ok("User created successfully. We send a verification email to the email account you entered. Please follow the email's instruction to verify your email.Unverified account and customer profile will be deleted by the end of account's creation day");
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> authenticate(@RequestBody LoginUserDto loginUserDto)
    {
        Account authenticatedUser = authenticationService.authenticate(loginUserDto);
        String jwtToken = jwtService.generateToken(authenticatedUser);
        LoginResponse loginResponse = new LoginResponse(authenticatedUser,jwtToken, jwtService.getExpirationTime());
        jwtTokenBlacklistService.addTokenForUser(authenticatedUser.getUsername(), jwtToken);
        return ResponseEntity.ok(loginResponse);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String token)
    {
        String jwt = token.substring(7);
        jwtTokenBlacklistService.blacklistToken(jwt);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/invalidate")
    public ResponseEntity<?> invalidateTokens(@RequestBody String username)
    {
        jwtTokenBlacklistService.invalidateTokensForUser(username);
        return ResponseEntity.ok().build();
    }
}
