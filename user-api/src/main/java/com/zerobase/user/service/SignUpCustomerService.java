package com.zerobase.user.service;

import com.zerobase.user.domain.SignUpForm;
import com.zerobase.user.model.Customer;
import com.zerobase.user.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SignUpCustomerService {

    private final CustomerRepository customerRepository;

    public Customer signUp(SignUpForm form) {
        return customerRepository.save(Customer.from(form));
    }
}
