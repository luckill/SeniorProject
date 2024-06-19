package com.example.SeniorProject;

import com.example.SeniorProject.Model.*;

public class LoginResponse
{
    private Account authenticateAccount;
    private String token;
    private long expiresIn;

    public LoginResponse(Account authenticateAccount, String token, long expiresIn)
    {
        this.authenticateAccount = authenticateAccount;
        this.token = token;
        this.expiresIn = expiresIn;
    }
    public String getToken()
    {
        return token;
    }

    public LoginResponse setToken(String token)
    {
        this.token = token;
        return this;
    }

    public long getExpiresIn()
    {
        return expiresIn;
    }

    public LoginResponse setExpiresIn(long expiresIn)
    {
        this.expiresIn = expiresIn;
        return this;
    }

    public Account getAuthenticateAccount()
    {
        return authenticateAccount;
    }

    public void setAuthenticateAccount(Account authenticateAccount)
    {
        this.authenticateAccount = authenticateAccount;
    }

    @Override
    public String toString()
    {
        return "LoginResponse{" +
            "token='" + token + '\'' +
            ", expiresIn=" + expiresIn +
            '}';
    }
}
