package com.zerobase.user.service;

import static com.zerobase.user.exception.ErrorCode.ALREADY_REGISTER_USER;

import com.zerobase.user.client.MailgunClient;
import com.zerobase.user.client.mailgun.SendMailForm;
import com.zerobase.user.domain.SignUpForm;
import com.zerobase.user.exception.CustomException;
import com.zerobase.user.exception.ErrorCode;
import com.zerobase.user.model.Customer;
import com.zerobase.user.model.Seller;
import com.zerobase.user.service.customer.SignUpCustomerService;
import com.zerobase.user.service.seller.SignUpSellerService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SignUpService {

    private final MailgunClient mailgunClient;
    private final SignUpCustomerService signUpCustomerService;
    private final SignUpSellerService signUpSellerService;

    public void customerVerify(String email, String code) {
        signUpCustomerService.verifyEmail(email, code);
    }

    public String customerSignUp(SignUpForm form) {
        if (signUpCustomerService.isEmailExist(form.getEmail())) {
            throw new CustomException(ALREADY_REGISTER_USER);
        }
        Customer customer = signUpCustomerService.signUp(form);
        String code = getRandomCode();
        SendMailForm sendMailForm = SendMailForm.builder().from("tester@gmail.com")
                .to(form.getEmail())
                .subject("Verification Email")
                .text(getVerificationEmailBody(form.getEmail(), form.getName(), "customer", code))
                .build();
        mailgunClient.sendEmail(sendMailForm);
        signUpCustomerService.changeCustomerValidateEmail(customer.getId(), code);
        return "회원 가입에 성공하였습니다.";
    }

    public void sellerVerify(String email, String code) {
        signUpSellerService.verifyEmail(email, code);
    }

    public String sellerSignUp(SignUpForm form) {
        if (signUpSellerService.isEmailExist(form.getEmail())) {
            throw new CustomException(ALREADY_REGISTER_USER);
        }

        Seller seller = signUpSellerService.signUp(form);
        String code = getRandomCode();
        SendMailForm sendMailForm = SendMailForm.builder()
                .from("tester@gmail.com")
                .to(form.getEmail())
                .subject("Verification Email")
                .text(getVerificationEmailBody(form.getEmail(), form.getName(), "seller", code))
                .build();
        mailgunClient.sendEmail(sendMailForm);
        signUpSellerService.changeSellerValidateEmail(seller.getId(), code);
        return "회원 가입에 성공하였습니다.";
    }

    private String getRandomCode() {
        return RandomStringUtils.random(10, true, true);
    }

    private String getVerificationEmailBody(String email, String name, String type, String code) {
        StringBuilder sb = new StringBuilder();
        return sb.append("Hello ")
                .append(name)
                .append("! Please Click Link for verification.\n\n")
                .append("http://localhost:8081/signup/").append(type).append("/verify?email=")
                .append(email)
                .append("&code=")
                .append(code).toString();
    }
}