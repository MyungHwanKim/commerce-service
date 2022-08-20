package com.zerobase.user.service;

import com.zerobase.domain.config.JwtAuthenticationProvider;
import com.zerobase.domain.domain.common.UserType;
import com.zerobase.user.domain.SignInForm;
import com.zerobase.user.exception.CustomException;
import com.zerobase.user.model.Customer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.zerobase.user.exception.ErrorCode.LOGIN_CHECK_FAIL;

@Service
@RequiredArgsConstructor
public class SignInService {

    private final CustomerService customerService;
    private final JwtAuthenticationProvider provider;

    public String customerLoginToken(SignInForm form) {
        Customer customer = customerService.findValidCustomer(form.getEmail(), form.getPassword())
                .orElseThrow(() -> new CustomException(LOGIN_CHECK_FAIL));
        return provider.createToken(customer.getEmail(), customer.getId(), UserType.CUSTOMER);
    }
}
