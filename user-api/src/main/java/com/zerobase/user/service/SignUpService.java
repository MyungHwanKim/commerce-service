package com.zerobase.user.service;

import com.zerobase.user.client.MailgunClient;
import com.zerobase.user.client.mailgun.SendMailForm;
import com.zerobase.user.domain.SignUpForm;
import com.zerobase.user.exception.CustomException;
import com.zerobase.user.exception.ErrorCode;
import com.zerobase.user.model.Customer;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class SignUpService {

    private final MailgunClient mailgunClient;
    private final SignUpCustomerService signUpCustomerService;

    public void customerVerify(String email, String code) {
        signUpCustomerService.verifyEmail(email, code);
    }

    public String customerSignUp(SignUpForm form) {
        if (signUpCustomerService.isEmailExist(form.getEmail())) {
            throw new CustomException(ErrorCode.ALREADY_REGISTER_USER);
        }
        Customer customer = signUpCustomerService.signUp(form);
        String code = getRandomCode();
        SendMailForm sendMailForm = SendMailForm.builder().from("tester@gmail.com")
                .to(form.getEmail())
                .subject("Verification Email")
                .text(getVerificationEmailBody(form.getEmail(), form.getName(), getRandomCode()))
                .build();
        mailgunClient.sendEmail(sendMailForm);
        signUpCustomerService.changeCustomerValidateEmail(customer.getId(), code);
        return "회원 가입에 성공하였습니다.";
    }

    private String getRandomCode() {
        return RandomStringUtils.random(10, true, true);
    }

    private String getVerificationEmailBody(String email, String name, String code) {
        StringBuilder sb = new StringBuilder();
        return sb.append("Hello ")
                .append(name)
                .append("! Please Click Link for verification.\n\n")
                .append("http://localhost:8081/signup//verify/customer?email=")
                .append(email)
                .append("&code=")
                .append(code).toString();
    }
}