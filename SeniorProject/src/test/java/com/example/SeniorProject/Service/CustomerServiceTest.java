package com.example.SeniorProject.Service;

import com.example.SeniorProject.Model.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.*;
import org.mockito.*;
import org.mockito.junit.jupiter.*;
import org.springframework.http.*;
import org.springframework.web.server.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CustomerServiceTest
{

    @InjectMocks
    private CustomerService customerService;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private AccountRepository accountRepository;

    @Test
    void testCheckIfAccountExistWithAccountExists()
    {
        String email = "test@example.com";
        Account account = new Account(); // Assume Account has a no-arg constructor
        when(accountRepository.findAccountByEmail(email)).thenReturn(account);

        Account result = customerService.checkIfAccountExist(email);
        assertNotNull(result);
        assertEquals(account, result);
    }

    @Test
    void testCheckIfAccountExistWithAccountNotFound()
    {
        String email = "test@example.com";
        when(accountRepository.findAccountByEmail(email)).thenReturn(null);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () ->
        {
            customerService.checkIfAccountExist(email);
        });
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("Account not found", exception.getReason());
    }

    @Test
    void testGetUserByIdCustomerExists()
    {
        int id = 1;
        Customer customer = new Customer(); // Assume Customer has a no-arg constructor
        when(customerRepository.findById(id)).thenReturn(Optional.of(customer));

        Customer result = customerService.getUserById(id);
        assertNotNull(result);
        assertEquals(customer, result);
    }

    @Test
    void testGetUserByIdCustomerNotFound()
    {
        int id = 1;
        when(customerRepository.findById(id)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () ->
        {
            customerService.getUserById(id);
        });
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("Customer not found", exception.getReason());
    }

    @Test
    void testUpdateCustomerExistingCustomer()
    {
        Customer existingCustomer = new Customer();
        existingCustomer.setId(1);
        existingCustomer.setFirstName("Old Name");

        Customer updatedCustomer = new Customer();
        updatedCustomer.setId(1);

        when(customerRepository.findById(1)).thenReturn(Optional.of(existingCustomer));

        customerService.updateCustomer(updatedCustomer);

        assertEquals("Old Name", existingCustomer.getFirstName());
    }

    @Test
    public void testDeleteCustomerAccountExists() {
        // Arrange
        int id = 1;
        Account account = new Account(); // Create a mock Account object
        when(accountRepository.findById(id)).thenReturn(Optional.of(account));

        // Act
        String result = customerService.deleteCustomer(id);

        // Assert
        assertEquals("Your customer profile has been successfully deleted", result);
        verify(customerRepository).deleteById(id); // Verify that deleteById was called
    }

    @Test
    public void testDeleteCustomerAccountNotFound() {
        // Arrange
        int id = 1;
        when(accountRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            customerService.deleteCustomer(id);
        });
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("Account not found", exception.getReason());
        verify(customerRepository, never()).deleteById(anyInt()); // Ensure deleteById was not called
    }

    @Test
    public void testUpdateCustomerCustomerExists() {
        // Arrange
        Customer existingCustomer = new Customer();
        existingCustomer.setId(1);
        existingCustomer.setFirstName("John");
        existingCustomer.setLastName("Doe");

        Customer updatedCustomer = new Customer();
        updatedCustomer.setId(1);
        updatedCustomer.setFirstName("Jane"); // Change first name

        when(customerRepository.findById(updatedCustomer.getId())).thenReturn(Optional.of(existingCustomer));

        // Act
        customerService.updateCustomer(updatedCustomer);

        // Assert
        assertEquals("Jane", existingCustomer.getFirstName()); // Verify first name is updated
        verify(customerRepository).save(existingCustomer); // Verify save was called
    }

    @Test
    public void testUpdateCustomer_WithNullFields() {
        // Arrange
        Customer existingCustomer = new Customer();
        existingCustomer.setId(1);
        existingCustomer.setFirstName("John");
        existingCustomer.setLastName("Doe");

        Customer updatedCustomer = new Customer();
        updatedCustomer.setId(1);
        updatedCustomer.setFirstName(null); // Not updating first name

        when(customerRepository.findById(updatedCustomer.getId())).thenReturn(Optional.of(existingCustomer));

        // Act
        customerService.updateCustomer(updatedCustomer);

        // Assert
        assertEquals("John", existingCustomer.getFirstName()); // Verify first name remains unchanged
        assertEquals("Doe", existingCustomer.getLastName()); // Verify last name remains unchanged
        verify(customerRepository).save(existingCustomer); // Verify save was called
    }

    @Test
    public void testUpdateCustomer_Success() {
        // Arrange
        Customer existingCustomer = new Customer();
        existingCustomer.setId(1);
        existingCustomer.setFirstName("John");
        existingCustomer.setLastName("Doe");
        existingCustomer.setPhone("1234567890");

        Customer updatedCustomer = new Customer();
        updatedCustomer.setId(1);
        updatedCustomer.setFirstName("Jane");
        updatedCustomer.setLastName(null); // This should remain unchanged
        updatedCustomer.setPhone("0987654321");

        when(customerRepository.findById(1)).thenReturn(Optional.of(existingCustomer));

        // Act
        customerService.updateCustomer(updatedCustomer);

        // Assert
        assertEquals("Jane", existingCustomer.getFirstName());
        assertEquals("Doe", existingCustomer.getLastName()); // Last name should not change
        assertEquals("0987654321", existingCustomer.getPhone());
        verify(customerRepository).save(existingCustomer); // Verify that save was called
    }

    @Test
    public void testUpdateCustomer_CustomerNotFound() {
        // Arrange
        Customer updatedCustomer = new Customer();
        updatedCustomer.setId(1);

        when(customerRepository.findById(1)).thenReturn(Optional.empty());

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            customerService.updateCustomer(updatedCustomer);
        });
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("Customer not found", exception.getReason());
        verify(customerRepository, never()).save(any()); // Ensure save was not called
    }

    @Test
    public void testUpdateCustomer_ExceptionDuringUpdate() {
        // Arrange
        Customer existingCustomer = new Customer();
        existingCustomer.setId(1);
        Customer updatedCustomer = new Customer();
        updatedCustomer.setId(1);

        when(customerRepository.findById(1)).thenReturn(Optional.of(existingCustomer));
        doThrow(new RuntimeException("Update failed")).when(customerRepository).save(existingCustomer);

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            customerService.updateCustomer(updatedCustomer);
        });
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getStatusCode());
        assertEquals("An error occurred while updating the customer", exception.getReason());
    }

    @Test
    public void testUpdateCustomer_WithAllNullFields() {
        // Arrange
        Customer existingCustomer = new Customer();
        existingCustomer.setId(1);
        existingCustomer.setFirstName("John");
        existingCustomer.setLastName("Doe");
        existingCustomer.setPhone("1234567890");
        Customer updatedCustomer = new Customer();
        updatedCustomer.setId(1);
        updatedCustomer.setFirstName(null);
        updatedCustomer.setLastName(null);
        updatedCustomer.setPhone(null);

        when(customerRepository.findById(1)).thenReturn(Optional.of(existingCustomer));

        // Act
        customerService.updateCustomer(updatedCustomer);

        // Assert
        assertEquals("John", existingCustomer.getFirstName());
        assertEquals("Doe", existingCustomer.getLastName());
        assertEquals("1234567890", existingCustomer.getPhone());
        verify(customerRepository).save(existingCustomer); // Verify that save was called
    }

    @Test
    public void testUpdateCustomer_WithIndividualNullFields() {
        // Arrange
        Customer existingCustomer = new Customer();
        existingCustomer.setId(1);
        existingCustomer.setFirstName("John");
        existingCustomer.setLastName("Doe");
        existingCustomer.setPhone("1234567890");

        Customer updatedCustomer = new Customer();
        updatedCustomer.setId(1);
        updatedCustomer.setFirstName(null); // No change
        updatedCustomer.setLastName("Smith"); // Change
        updatedCustomer.setPhone(null); // No change

        when(customerRepository.findById(1)).thenReturn(Optional.of(existingCustomer));

        // Act
        customerService.updateCustomer(updatedCustomer);

        // Assert
        assertEquals("John", existingCustomer.getFirstName()); // Should not change
        assertEquals("Smith", existingCustomer.getLastName()); // Should change
        assertEquals("1234567890", existingCustomer.getPhone()); // Should not change
        verify(customerRepository).save(existingCustomer); // Verify that save was called
    }
}
