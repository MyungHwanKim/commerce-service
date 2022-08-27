package com.zerobase.user.service.seller;

import com.zerobase.user.domain.SignUpForm;
import com.zerobase.user.exception.CustomException;
import com.zerobase.user.model.Seller;
import com.zerobase.user.repository.SellerRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static com.zerobase.user.exception.ErrorCode.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class SignUpSellerServiceTest {

    @Mock
    private SellerRepository sellerRepository;

    @InjectMocks
    private SignUpSellerService signUpSellerService;

    @Test
    @DisplayName("처음 회원 가입하는 경우(셀러) - 회원 가입 성공")
    void signUpTest() {
        // given
        SignUpForm form = SignUpForm.builder()
                .name("choi")
                .birth(LocalDate.now())
                .email("kmh9250@gmail.com")
                .password("1234")
                .phone("01000000000")
                .build();
        given(sellerRepository.save(any()))
                .willReturn(Seller.from(form));

        // when
        Seller seller = signUpSellerService.signUp(form);

        // then
        assertEquals("choi", seller.getName());
        assertEquals("kmh9250@gmail.com", seller.getEmail());
        assertEquals("01000000000", seller.getPhone());
    }

    @Test
    @DisplayName("회원 가입하는 경우(이메일 존재)(셀러) - 회원 가입 실패")
    void isEmailExistTest() {
        // given
        Seller choi = Seller.builder()
                .id(1L)
                .email("kmh9250@gmail.com")
                .name("choi")
                .password("1234")
                .phone("01000000000")
                .birth(LocalDate.now())
                .build();
        given(sellerRepository.findByEmail(anyString()))
                .willReturn(Optional.of(choi));

        // when
        SignUpForm form = SignUpForm.builder()
                .name("choi")
                .birth(LocalDate.now())
                .email("kmh9250@gmail.com")
                .password("1234")
                .phone("01000000000")
                .build();

        boolean isEmailExist = signUpSellerService.isEmailExist(form.getEmail());

        // then
        assertTrue(isEmailExist);
    }

    @Test
    @DisplayName("이메일 인증하는 경우(셀러) - 인증 성공")
    void verifyEmailTest() {
        // given
        Seller choi = Seller.builder()
                .id(3L)
                .email("kmh9250@naver.com")
                .name("choi")
                .password("1234")
                .phone("01012341234")
                .birth(LocalDate.now())
                .verify(false)
                .build();
        given(sellerRepository.findById(anyLong()))
                .willReturn(Optional.of(choi));
        given(sellerRepository.findByEmail(anyString()))
                .willReturn(Optional.of(choi));

        // when
        signUpSellerService.changeSellerValidateEmail(3L, "1234");
        signUpSellerService.verifyEmail("kmh9250@gmail.com", "1234");

        // then
        assertTrue(choi.isVerify());
    }

    @Test
    @DisplayName("이메일 인증하는 경우(인증 완료)(셀러) - 인증 실패")
    void alreadyVerifyTest() {
        // given
        Seller choi = Seller.builder()
                .id(3L)
                .email("kmh9250@naver.com")
                .name("choi")
                .password("1234")
                .phone("01012341234")
                .birth(LocalDate.now())
                .verify(true)
                .build();
        given(sellerRepository.findById(anyLong()))
                .willReturn(Optional.of(choi));
        given(sellerRepository.findByEmail(anyString()))
                .willReturn(Optional.of(choi));

        // when
        signUpSellerService.changeSellerValidateEmail(3L, "1234");
        CustomException exception = assertThrows(CustomException.class,
                () -> signUpSellerService.verifyEmail("kmh9250@gmail.com", "1234"));

        // then
        assertEquals(ALREADY_VERIFY, exception.getErrorCode());
    }

    @Test
    @DisplayName("이메일 인증하는 경우(코드 불일치)(셀러) - 인증 실패")
    void wrongVerificationTest() {
        // given
        Seller choi = Seller.builder()
                .id(3L)
                .email("kmh9250@naver.com")
                .name("choi")
                .password("1234")
                .phone("01012341234")
                .birth(LocalDate.now())
                .verify(false)
                .build();
        given(sellerRepository.findById(anyLong()))
                .willReturn(Optional.of(choi));
        given(sellerRepository.findByEmail(anyString()))
                .willReturn(Optional.of(choi));

        // when
        signUpSellerService.changeSellerValidateEmail(3L, "1234");
        CustomException exception = assertThrows(CustomException.class,
                () -> signUpSellerService.verifyEmail("kmh9250@gmail.com", "5678"));

        // then
        assertEquals(WRONG_VERIFICATION, exception.getErrorCode());
    }

    @Test
    @DisplayName("이메일 인증하는 경우(코드 만료)(셀러) - 인증 실패")
    void expireCodeTest() {
        // given
        Seller choi = Seller.builder()
                .id(3L)
                .email("kmh9250@naver.com")
                .name("choi")
                .password("1234")
                .phone("01012341234")
                .birth(LocalDate.now())
                .verify(false)
                .verificationCode("1234")
                .verifyExpiredAt(LocalDateTime.now().minusDays(2))
                .build();
        given(sellerRepository.findByEmail(anyString()))
                .willReturn(Optional.of(choi));

        // when
        CustomException exception = assertThrows(CustomException.class,
                () -> signUpSellerService.verifyEmail("kmh9250@gmail.com", "1234"));

        // then
        assertEquals(EXPIRE_CODE, exception.getErrorCode());
    }
}