package com.zerobase.user.service.seller;


import com.zerobase.user.domain.SignUpForm;
import com.zerobase.user.exception.CustomException;
import com.zerobase.user.exception.ErrorCode;
import com.zerobase.user.model.Seller;
import com.zerobase.user.repository.SellerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Locale;

import static com.zerobase.user.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class SignUpSellerService {

    private final SellerRepository sellerRepository;

    public Seller signUp(SignUpForm form) {
        return sellerRepository.save(Seller.from(form));
    }

    public boolean isEmailExist(String email) {
        return sellerRepository.findByEmail(email.toLowerCase(Locale.ROOT))
                .isPresent();
    }

    @Transactional
    public void verifyEmail(String email, String code) {
        Seller seller = sellerRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(NOT_FOUND_USER));

        if (seller.isVerify()) {
            throw new CustomException(ALREADY_VERIFY);
        }

        if (!seller.getVerificationCode().equals(code)) {
            throw new CustomException(WRONG_VERIFICATION);
        }

        if (seller.getVerifyExpiredAt().isBefore(LocalDateTime.now())) {
            throw new CustomException(EXPIRE_CODE);
        }

        seller.setVerify(true);
    }

    @Transactional
    public LocalDateTime changeSellerValidateEmail(Long sellerId, String verificationCode) {
        Seller seller = sellerRepository.findById(sellerId)
                .orElseThrow(() -> new CustomException(NOT_FOUND_USER));

        seller.setVerificationCode(verificationCode);
        seller.setVerifyExpiredAt(LocalDateTime.now().plusDays(1));
        return seller.getVerifyExpiredAt();
    }
}
