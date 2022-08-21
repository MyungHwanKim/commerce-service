package com.zerobase.user.service.customer;

import com.zerobase.user.domain.SignUpForm;
import com.zerobase.user.exception.CustomException;
import com.zerobase.user.model.Customer;
import com.zerobase.user.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Optional;

import static com.zerobase.user.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class SignUpCustomerService {

    private final CustomerRepository customerRepository;

    public Customer signUp(SignUpForm form) {
        return customerRepository.save(Customer.from(form));
    }

    public boolean isEmailExist(String email) {
        return customerRepository.findByEmail(email.toLowerCase(Locale.ROOT))
                .isPresent();
    }

    @Transactional
    public void verifyEmail(String email, String code) {
        Customer customer = customerRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(NOT_FOUND_USER));

        if (customer.isVerify()) {
            throw new CustomException(ALREADY_VERIFY);
        }

        if (!customer.getVerificationCode().equals(code)) {
            throw new CustomException(WRONG_VERIFICATION);
        }

        if (customer.getVerifyExpiredAt().isBefore(LocalDateTime.now())) {
            throw new CustomException(EXPIRE_CODE);
        }

        customer.setVerify(true);
    }

    @Transactional
    public LocalDateTime changeCustomerValidateEmail(Long customId, String verificationCode) {
        Customer customer = customerRepository.findById(customId)
                .orElseThrow(() -> new CustomException(NOT_FOUND_USER));

        customer.setVerificationCode(verificationCode);
        customer.setVerifyExpiredAt(LocalDateTime.now().plusDays(1));
        return customer.getVerifyExpiredAt();
    }
}
