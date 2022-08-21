package com.zerobase.user.service;

import com.zerobase.user.domain.SignUpForm;
import com.zerobase.user.model.Customer;
import com.zerobase.user.service.customer.SignUpCustomerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class SignUpCustomerServiceTest {

    @Autowired
    private SignUpCustomerService signUpCustomerService;

    @Test
    void SignUp() {
        // given
        SignUpForm form = SignUpForm.builder()
                .name("name")
                .birth(LocalDate.now())
                .email("abc@gmail.com")
                .password("1234")
                .phone("01000000000")
                .build();

        // when
        Customer customer = signUpCustomerService.signUp(form);

        // then
        assertNotNull(customer.getId());
        assertEquals("name", customer.getName());
        assertEquals("abc@gmail.com", customer.getEmail());
        assertEquals("01000000000", customer.getPhone());
        assertNotNull(customer.getCreatedAt());
    }
}