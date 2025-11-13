package com.example.server.controller;

import com.example.common.api.CustomerApiContract;
import com.example.common.dto.CustomerDTO;
import com.example.server.service.CustomerService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/customers")
public class CustomerController implements CustomerApiContract {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @Override
    @GetMapping
    public List<CustomerDTO> listCustomers() {
        return customerService.findAll();
    }

    @Override
    @GetMapping("/{id}")
    public Optional<CustomerDTO> findById(@PathVariable Long id) {
        return customerService.findById(id);
    }

    @Override
    @PostMapping
    public CustomerDTO create(@RequestBody CustomerDTO customer) {
        return customerService.create(customer);
    }

    @Override
    @PutMapping("/{id}")
    public CustomerDTO update(@PathVariable Long id, @RequestBody CustomerDTO customer) {
        return customerService.update(id, customer)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente non trovato"));
    }

    @Override
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        boolean deleted = customerService.delete(id);
        if (!deleted) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente non trovato");
        }
    }
}
