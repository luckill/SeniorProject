package com.example.SeniorProject.Configuration;

import com.example.SeniorProject.Model.*;
import org.springframework.context.annotation.*;
import org.springframework.security.authentication.*;
import org.springframework.security.authentication.dao.*;
import org.springframework.security.config.annotation.authentication.configuration.*;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.bcrypt.*;

@Configuration
public class ApplicationConfiguration
{
    private final AccountRepository accountRepository;

    public ApplicationConfiguration(AccountRepository accountRepository)
    {
        this.accountRepository = accountRepository;
    }

    @Bean
    UserDetailsService userDetailsService()
    {
        return username ->
        {
            Account account = accountRepository.findAccountByEmail(username);
            if (account == null)
            {
                throw new UsernameNotFoundException("User not found");
            }
            return account;
        };
    }

    @Bean
    BCryptPasswordEncoder passwordEncoder()
    {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration)
    {
        try
        {
            return configuration.getAuthenticationManager();
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    @Bean
    AuthenticationProvider authenticationProvider()
    {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService());
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }
}
