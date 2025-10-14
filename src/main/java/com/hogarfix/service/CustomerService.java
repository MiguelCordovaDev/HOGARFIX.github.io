package com.hogarfix.service;

import com.hogarfix.model.Customer;
import com.hogarfix.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomerService {
    private final CustomerRepository clienteRepository;

    public Optional<Customer> findById(Long id) {
        return clienteRepository.findById(id);
    }

    public Customer save(Customer cliente) {
        return clienteRepository.save(cliente);
    }
}
