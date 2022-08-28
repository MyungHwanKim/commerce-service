package com.zerobase.user.service.customer;

import com.zerobase.user.model.Customer;
import com.zerobase.user.repository.CustomerRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;
    @InjectMocks
    private CustomerService customerService;

    @Test
    @DisplayName("로그인 토큰을 위한 인증을 확인할 경우(고객) - 로그인 인증 성공")
    void findValidCustomerTest() {
        // given
        Customer choi = Customer.builder()
                .id(1L)
                .email("kmh9250@naver.com")
                .name("choi")
                .password("1234")
                .phone("01012341234")
                .birth(LocalDate.now())
                .verify(true)
                .build();
        given(customerRepository.findByEmail(anyString()))
                .willReturn(Optional.of(choi));

        // when
        Optional<Customer> customer = customerService.findValidCustomer("kmh9250@naver.com", "1234");

        // then
        assertEquals(choi.getEmail(), customer.get().getEmail());
        assertEquals(choi.getPassword(), customer.get().getPassword());
        assertTrue(customer.get().isVerify());
    }

    @Test
    @DisplayName("로그인 토큰을 위한 인증을 확인할 경우(고객) - 로그인 인증 실패")
    void loginCheckFailTest() {
        // given
        Customer choi = Customer.builder()
                .id(3L)
                .email("abc@naver.com")
                .name("choi")
                .password("1234")
                .phone("01012341234")
                .birth(LocalDate.now())
                .verify(false)
                .build();
        given(customerRepository.findByEmail(anyString()))
                .willReturn(Optional.of(choi));

        // when
        Optional<Customer> validCustomer1 = customerService.findValidCustomer("abc@gmail.com", "1234");
        Optional<Customer> validCustomer2 = customerService.findValidCustomer("abc@naver.com", "5678");
        Optional<Customer> validCustomer3 = customerService.findValidCustomer("abc@naver.com", "1234");

        // then
        assertThat(validCustomer1).isEmpty();
        assertThat(validCustomer2).isEmpty();
        assertThat(validCustomer3).isEmpty();
    }
}