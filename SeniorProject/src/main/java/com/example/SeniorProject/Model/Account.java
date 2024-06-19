package com.example.SeniorProject.Model;

import jakarta.persistence.*;

import com.fasterxml.jackson.annotation.JsonBackReference;
import org.springframework.security.core.*;
import org.springframework.security.core.userdetails.*;

import javax.validation.constraints.*;
import java.util.*;
import com.example.SeniorProject.Model.Customer;


@Entity
@Table(name = "account")
public class Account implements UserDetails
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "account_id")
    private int id;
    @NotNull
    @Column(name = "email")
    private String email;
    @NotNull
    @Size(min = 8, message = "password must be at least 8 character long")
    @Column(name = "password")
    private String password;
    @NotNull
    @Column(name = "is_admin")
    private boolean isAdmin;
    @NotNull
    @Column(name = "is_verified")
    private boolean isVerified;
    @OneToOne(mappedBy = "account")
    @JsonBackReference
    private Customer customer;

    public Account()
    {

    }

    public Account(String email, String password, boolean isAdmin)
    {
        this.email = email;
        this.password = password;
        this.isAdmin = isAdmin;
    }

    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    public boolean isAdmin()
    {
        return isAdmin;
    }

    public void setAdmin(boolean admin) 
    {
        isAdmin = admin;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities()
    {
        return List.of();
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    public int getId()
    {
        return id;
    }

    @Override
    public String getUsername()
    {
        return email;
    }

    @Override
    public boolean isAccountNonExpired()
    {
        return true;
    }

    @Override
    public boolean isAccountNonLocked()
    {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired()
    {
        return true;
    }

    @Override
    public boolean isEnabled()
    {
        return true;
    }
}
