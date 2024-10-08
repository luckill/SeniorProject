package com.example.SeniorProject.Controller;

import com.example.SeniorProject.DTOs.LoginUserDTO;
import com.example.SeniorProject.DTOs.RegisterUserDTO;
import com.example.SeniorProject.Exception.BadRequestException;
import com.example.SeniorProject.LoginResponse;
import com.example.SeniorProject.Model.Account;
import com.example.SeniorProject.Service.AuthenticationService;
import com.example.SeniorProject.Service.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.LockedException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/auth")
@RestController
public class AuthenticaitonController
{
	private final JwtService jwtService;
	private final AuthenticationService authenticationService;

	public AuthenticaitonController(JwtService jwtService, AuthenticationService authenticationService)
    {
        this.jwtService = jwtService;
        this.authenticationService = authenticationService;
    }

    @PostMapping("/signup")
    public ResponseEntity<?> register(@RequestBody RegisterUserDTO registerUserDTO)
    {
        authenticationService.signUp(registerUserDTO);
        return ResponseEntity.ok("User created successfully. We send a verification email to the email account you entered. Please follow the email's instruction to verify your email.Unverified account and customer profile will be deleted at 12am Pacific Time");
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticate(@RequestBody LoginUserDTO loginUserDTO)
    {
        try
        {
            Account authenticatedUser = authenticationService.authenticate(loginUserDTO);
            String jwtToken = jwtService.generateToken(authenticatedUser);
            LoginResponse loginResponse = new LoginResponse(authenticatedUser,jwtToken, jwtService.getExpirationTime());
            return ResponseEntity.ok(loginResponse);
        }
        catch (BadRequestException exception)
        {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid credentials provided.");
        }
        catch (LockedException exception)
        {
            return ResponseEntity.status(HttpStatus.LOCKED).body("you account id locked");
        }
    }
}