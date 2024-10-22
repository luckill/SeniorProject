package com.example.SeniorProject.Service;

import com.example.SeniorProject.Model.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.http.*;
import org.springframework.stereotype.*;
import org.springframework.web.server.*;

import java.util.*;

@Service
public class CustomerService
{
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private AccountRepository accountRepository;

    public Account checkIfAccountExist(String email)
    {
        Account account = accountRepository.findAccountByEmail(email);
        if (account != null)
        {
            return account;
        }
        else
        {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found");
        }
    }

    public Customer getUserById(int id)
    {
        Customer customer = customerRepository.findById(id).orElse(null);
        if (customer == null)
        {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found");
        }
        return customer;
    }

    public void updateCustomer(Customer customer)
    {

        // Fetch existing customer from the database
        Customer existingCustomer = customerRepository.findById(customer.getId()).orElse(null);

        if (existingCustomer == null)
        {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found");
        }

        // Update fields if they are not null
        if (customer.getFirstName() != null && (!existingCustomer.getFirstName().equals(customer.getFirstName())))
        {
            existingCustomer.setFirstName(customer.getFirstName());
        }
        if (customer.getLastName() != null && (!existingCustomer.getLastName().equals(customer.getLastName())))
        {
            existingCustomer.setLastName(customer.getLastName());
        }
        if (customer.getPhone() != null && (!existingCustomer.getPhone().equals(customer.getPhone())))
        {
            existingCustomer.setPhone(customer.getPhone());
        }
        try
        {
            customerRepository.save(existingCustomer);
        }
        // Save updated customer
        catch(Exception ex)
        {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "An error occurred while updating the customer");
        }
    }

    public String deleteCustomer(int id)
    {
        Optional<Account> account = accountRepository.findById(id);
        if (account.isEmpty())
        {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found");
        }
        customerRepository.deleteById(id);
        return "Your customer profile has been successfully deleted";
    }
}
