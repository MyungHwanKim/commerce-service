package com.zerobase.user.service.customer;

import com.zerobase.user.domain.SignUpForm;
import com.zerobase.user.exception.CustomException;
import com.zerobase.user.model.Customer;
import com.zerobase.user.repository.CustomerRepository;
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
class SignUpCustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private SignUpCustomerService signUpCustomerService;

    @Test
    @DisplayName("처음 회원 가입하는 경우(고객) - 회원 가입 성공")
    void signUpTest() {
        // given
        SignUpForm form = SignUpForm.builder()
                .name("choi")
                .birth(LocalDate.now())
                .email("kmh9250@gmail.com")
                .password("1234")
                .phone("01000000000")
                .build();
        given(customerRepository.save(any()))
                .willReturn(Customer.from(form));

        // when
        Customer customer = signUpCustomerService.signUp(form);

        // then
        assertEquals("choi", customer.getName());
        assertEquals("kmh9250@gmail.com", customer.getEmail());
        assertEquals("01000000000", customer.getPhone());
    }

    @Test
    @DisplayName("회원 가입하는 경우(이메일 존재)(고객) - 회원 가입 실패")
    void isEmailExistTest() {
        // given
        Customer choi = Customer.builder()
                .id(1L)
                .email("kmh9250@gmail.com")
                .name("choi")
                .password("1234")
                .phone("01000000000")
                .birth(LocalDate.now())
                .build();
        given(customerRepository.findByEmail(anyString()))
                .willReturn(Optional.of(choi));

        // when
        SignUpForm form = SignUpForm.builder()
                .name("choi")
                .birth(LocalDate.now())
                .email("kmh9250@gmail.com")
                .password("1234")
                .phone("01000000000")
                .build();

        boolean isEmailExist = signUpCustomerService.isEmailExist(form.getEmail());

        // then
        assertTrue(isEmailExist);
    }

    @Test
    @DisplayName("이메일 인증하는 경우(고객) - 인증 성공")
    void verifyEmailTest() {
        // given
        Customer choi = Customer.builder()
                .id(3L)
                .email("kmh9250@naver.com")
                .name("choi")
                .password("1234")
                .phone("01012341234")
                .birth(LocalDate.now())
                .verify(false)
                .build();
        given(customerRepository.findById(anyLong()))
                .willReturn(Optional.of(choi));
        given(customerRepository.findByEmail(anyString()))
                .willReturn(Optional.of(choi));

        // when
        signUpCustomerService.changeCustomerValidateEmail(3L, "1234");
        signUpCustomerService.verifyEmail("kmh9250@gmail.com", "1234");

        // then
        assertTrue(choi.isVerify());
    }

    @Test
    @DisplayName("이메일 인증하는 경우(인증 완료)(고객) - 인증 실패")
    void alreadyVerifyTest() {
        // given
        Customer choi = Customer.builder()
                .id(3L)
                .email("kmh9250@naver.com")
                .name("choi")
                .password("1234")
                .phone("01012341234")
                .birth(LocalDate.now())
                .verify(true)
                .build();
        given(customerRepository.findById(anyLong()))
                .willReturn(Optional.of(choi));
        given(customerRepository.findByEmail(anyString()))
                .willReturn(Optional.of(choi));

        // when
        signUpCustomerService.changeCustomerValidateEmail(3L, "1234");
        CustomException exception = assertThrows(CustomException.class,
                () -> signUpCustomerService.verifyEmail("kmh9250@gmail.com", "1234"));

        // then
        assertEquals(ALREADY_VERIFY, exception.getErrorCode());
    }

    @Test
    @DisplayName("이메일 인증하는 경우(코드 불일치)(고객) - 인증 실패")
    void wrongVerificationTest() {
        // given
        Customer choi = Customer.builder()
                .id(3L)
                .email("kmh9250@naver.com")
                .name("choi")
                .password("1234")
                .phone("01012341234")
                .birth(LocalDate.now())
                .verify(false)
                .build();
        given(customerRepository.findById(anyLong()))
                .willReturn(Optional.of(choi));
        given(customerRepository.findByEmail(anyString()))
                .willReturn(Optional.of(choi));

        // when
        signUpCustomerService.changeCustomerValidateEmail(3L, "1234");
        CustomException exception = assertThrows(CustomException.class,
                () -> signUpCustomerService.verifyEmail("kmh9250@gmail.com", "5678"));

        // then
        assertEquals(WRONG_VERIFICATION, exception.getErrorCode());
    }

    @Test
    @DisplayName("이메일 인증하는 경우(코드 만료)(고객) - 인증 실패")
    void expireCodeTest() {
        // given
        Customer choi = Customer.builder()
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
        given(customerRepository.findByEmail(anyString()))
                .willReturn(Optional.of(choi));

        // when
        CustomException exception = assertThrows(CustomException.class,
                () -> signUpCustomerService.verifyEmail("kmh9250@gmail.com", "1234"));

        // then
        assertEquals(EXPIRE_CODE, exception.getErrorCode());
    }
}