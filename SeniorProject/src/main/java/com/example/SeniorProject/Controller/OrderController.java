package com.example.SeniorProject.Controller;

import com.example.SeniorProject.Model.*;
import com.example.SeniorProject.DTOs.*;
import com.example.SeniorProject.Service.*;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.server.*;

import java.util.*;


@RestController
@RequestMapping(path = "/order")
public class OrderController
{

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private PdfService pdfService;

    @Autowired
    private OrderService orderService;

    @PostMapping("/create")
    @Transactional
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> createOrder(@RequestParam(name = "id") int id, @RequestBody OrderDTO orderDTO)
    {
        try
        {
            orderService.createOrder(id, orderDTO);
            return new ResponseEntity<>("An order has been successfully created, we will send an confirmation email to your email address soon", HttpStatus.OK);
        }
        catch (ResponseStatusException exception)
        {
            return ResponseEntity.status(exception.getStatusCode()).body(exception.getMessage());
        }
    }

    // Cancel an order
    @PutMapping("/cancel")
    public ResponseEntity<?> cancelOrder(@RequestParam int orderId)
    {
        try
        {
            orderService.cancelOrder(orderId);
            return new ResponseEntity<>("An order has been successfully cancelled", HttpStatus.OK);
        }
        catch (ResponseStatusException exception)
        {
            return ResponseEntity.status(exception.getStatusCode()).body(exception.getMessage());
        }
    }

    // Fetching the current orders for a customer (status = 'active')
    @GetMapping(path = "/currentOrders")
    public ResponseEntity<?> getCurrentOrders(@RequestParam int customerId)
    {
        try
        {
            List<OrderDTO> currentOrders = orderService.getCurrentOrders(customerId);
            return ResponseEntity.status(HttpStatus.OK).body(currentOrders);
        }
        catch (ResponseStatusException exception)
        {
            return ResponseEntity.status(exception.getStatusCode()).body(exception.getMessage());
        }
    }

    // Fetch past orders for a customer (status = 'completed')
    @GetMapping(path = "/pastOrders")
    public ResponseEntity<?> getPastOrders(@RequestParam int customerId)
    {
        try
        {
            List<OrderDTO> pastOrders = orderService.getPastOrders(customerId);
            return ResponseEntity.status(HttpStatus.OK).body(pastOrders);
        }
        catch (ResponseStatusException exception)
        {
            return ResponseEntity.status(exception.getStatusCode()).body(exception.getMessage());
        }
    }


    // Read all orders with pagination
    @GetMapping(path = "/getAll")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getAllOrders()
    {
        try
        {
            List<OrderDTO> orders = orderService.getAllOrders();
            return ResponseEntity.status(HttpStatus.OK).body(orders);
        }
        catch (ResponseStatusException exception)
        {
            return ResponseEntity.status(exception.getStatusCode()).body(exception.getMessage());
        }
    }

    // Read a single order by ID
    @GetMapping(path = "/getById")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getOrderById(@RequestParam int id)
    {
        try
        {
            OrderDTO order = orderService.getOrderById(id);
            return ResponseEntity.status(HttpStatus.OK).body(order);
        }
        catch (ResponseStatusException exception)
        {
            return ResponseEntity.status(exception.getStatusCode()).body(exception.getMessage());
        }
    }

    @PutMapping(path = "/update")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<?> updateOrder(@RequestParam int orderId, @RequestBody OrderDTO orderDTO)
    {
        try
        {
            orderService.updateOrder(orderId, orderDTO);
            return new ResponseEntity<>("The order has been successfully updated", HttpStatus.OK);
        }
        catch (ResponseStatusException exception)
        {
            return ResponseEntity.status(exception.getStatusCode()).body(exception.getMessage());
        }
    }

    // Delete an order
    @DeleteMapping(path = "/delete")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> deleteOrder(@RequestParam int id)
    {
        try
        {
            orderService.deleteOrder(id);
            return ResponseEntity.ok("Order deleted successfully");
        }
        catch (ResponseStatusException exception)
        {
            return ResponseEntity.status(exception.getStatusCode()).body(exception.getMessage());
        }
    }

    // Return an order
    @PostMapping(path = "/return")
    public ResponseEntity<?> returnOrder(@RequestParam int orderId)
    {
        try
        {
            orderService.returnOrder(orderId);
            return ResponseEntity.ok("Order returned successfully");
        }
        catch (ResponseStatusException exception)
        {
            return ResponseEntity.status(exception.getStatusCode()).body(exception.getMessage());
        }
    }

    @GetMapping(path = "/getOrderByCustomerId")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getOrderByCustomerId(@RequestParam int id)
    {
        try
        {
            List<OrderDTO> orders = orderService.getOrderByCustomerId(id);
            return ResponseEntity.status(HttpStatus.OK).body(orders);
        }
        catch (ResponseStatusException exception)
        {
            return ResponseEntity.status(exception.getStatusCode()).body(exception.getMessage());
        }
    }

    @GetMapping("/getCustomerByOrderId")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getCustomerByOrderId(@RequestParam int orderId)
    {
        try
        {
            CustomerDTO customer = orderService.getCustomerByOrderId(orderId);
            return ResponseEntity.status(HttpStatus.OK).body(customer);
        }
        catch (ResponseStatusException exception)
        {
            return ResponseEntity.status(exception.getStatusCode()).body(exception.getMessage());
        }
    }

    @GetMapping(path = "/generateInvoice")
    public ResponseEntity<ByteArrayResource> generateInvoice(@RequestParam int orderId)
    {
        // Fetch the order by ID
        Order order = orderRepository.findById(orderId).orElse(null);
        if (order == null)
        {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        // Fetch the customer associated with the order
        Customer customer = order.getCustomer();

        // Populate model for invoice
        Map<String, Object> model = new HashMap<>();
        model.put("order", order);
        model.put("customer", customer);

        // Generate the PDF using the PdfService
        ByteArrayResource pdfContent = pdfService.generateInvoicePDF(model);

        // Prepare the HTTP response with the generated PDF
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "invoice_" + orderId + ".pdf");

        // Return the PDF as a response entity
        return new ResponseEntity<>(pdfContent, headers, HttpStatus.OK);
    }
}