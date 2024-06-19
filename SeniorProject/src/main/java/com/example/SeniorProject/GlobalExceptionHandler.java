package com.example.SeniorProject;

import com.example.SeniorProject.Exception.*;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.http.*;
import org.springframework.security.authentication.*;
import org.springframework.web.bind.annotation.*;

import java.nio.file.*;

@RestControllerAdvice
public class GlobalExceptionHandler
{
    @ExceptionHandler(Exception.class)
    public ProblemDetail handleSecurityException(Exception exception)
    {
        ProblemDetail errorDetail = null;
        exception.printStackTrace();

        if(exception instanceof BadCredentialsException)
        {
            errorDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(401), exception.getMessage());
            errorDetail.setProperty("description", "The username or password is incorrect.");
            return errorDetail;
        }
        else if (exception instanceof AccountStatusException)
        {
            errorDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(403), exception.getMessage());
            errorDetail.setProperty("description", "The account is locked.");
            return errorDetail;
        }
        else if(exception instanceof AccessDeniedException)
        {
            errorDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(403), exception.getMessage());
            errorDetail.setProperty("description", "You are not allowed to access this resource.");
            return errorDetail;
        }
        else if(exception instanceof SignatureException)
        {
            errorDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(403), exception.getMessage());
            errorDetail.setProperty("description", "The JWT signature is invalid.");

            return errorDetail;
        }
        else if(exception instanceof ExpiredJwtException)
        {
            errorDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(403), exception.getMessage());
            errorDetail.setProperty("description", "The JWT token has expired.");
            return errorDetail;
        }
        else if (exception instanceof BadRequestException)
        {
            errorDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(401), exception.getMessage());
            errorDetail.setProperty("description", "account already existed");
            return errorDetail;
        }
        else
        {
            errorDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(500), exception.getMessage());
            errorDetail.setProperty("description", "Unknown internal server error.");
            return errorDetail;
        }
    }
}