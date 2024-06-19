package com.example.SeniorProject.Service;

import com.example.SeniorProject.DTOs.*;
import com.example.SeniorProject.Exception.*;
import com.example.SeniorProject.Model.*;
import org.springframework.security.authentication.*;
import org.springframework.security.crypto.password.*;
import org.springframework.stereotype.*;

@Service
public class AuthenticationService
{
    private final CustomerRepository customerRepository;
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public AuthenticationService(CustomerRepository customerRepository, AccountRepository accountRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager)
    {
        this.customerRepository = customerRepository;
        this.accountRepository = accountRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
    }

    public Customer signUp(RegisterUserDTO input)
    {
        if (accountRepository.findAccountByEmail(input.getEmail()) != null )
        {
            throw new BadRequestException("Account associated by this email already exists");
        }
        Customer customer = new Customer(input.getFirstName(), input.getLastName(), input.getEmail(), input.getPhoneNumber(),"","","","");
        Account account = new Account(input.getEmail(), passwordEncoder.encode(input.getPassword()), false);
        accountRepository.save(account);
        customer.setAccount(account);
        return customerRepository.save(customer);
    }

    public Account authenticate(LoginUserDto input)
    {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(input.getEmail(), input.getPassword()));
        return accountRepository.findAccountByEmail(input.getEmail());
    }
}
