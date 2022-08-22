package com.zerobase.user.controller;

import com.zerobase.user.domain.SignUpForm;
import com.zerobase.user.service.SignUpService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/signUp")
@RequiredArgsConstructor
public class SignUpController {

    private final SignUpService signUpService;


    @PostMapping("/customer")
    public ResponseEntity<String> customerSignUp(@RequestBody SignUpForm form) {
        return ResponseEntity.ok(signUpService.customerSignUp(form));
    }

    @GetMapping("/customer/verify")
    public ResponseEntity<String> verifyCustomer(String email, String code) {
        signUpService.customerVerify(email, code);
        return ResponseEntity.ok("인증이 완료되었습니다.");
    }

    @PostMapping("/seller")
    public ResponseEntity<String> sellerSignUp(@RequestBody SignUpForm form) {
        return ResponseEntity.ok(signUpService.sellerSignUp(form));
    }

    @GetMapping("/seller/verify")
    public ResponseEntity<String> verifySeller(String email, String code) {
        signUpService.sellerVerify(email, code);
        return ResponseEntity.ok("인증이 완료되었습니다.");
    }
}