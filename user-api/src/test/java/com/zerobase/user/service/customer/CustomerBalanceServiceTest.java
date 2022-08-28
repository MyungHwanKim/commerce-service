package com.zerobase.user.service.customer;

import com.zerobase.user.domain.customer.ChangeBalanceForm;
import com.zerobase.user.exception.CustomException;
import com.zerobase.user.model.Customer;
import com.zerobase.user.model.CustomerBalanceHistory;
import com.zerobase.user.repository.CustomerBalanceHistoryRepository;
import com.zerobase.user.repository.CustomerRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static com.zerobase.user.exception.ErrorCode.NOT_ENOUGH_BALANCE;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CustomerBalanceServiceTest {

    @Mock
    private CustomerBalanceHistoryRepository customerBalanceHistoryRepository;
    @Mock
    private CustomerRepository customerRepository;
    @InjectMocks
    private CustomerBalanceService customerBalanceService;

    @Test
    @DisplayName("고객의 잔액을 변경하는 경우 - 잔액 변경 성공")
    void changeBalanceTest() {
        // given
        Customer choi = Customer.builder()
                .id(1L)
                .email("kmh9250@naver.com")
                .name("choi")
                .password("1234")
                .phone("01012341234")
                .birth(LocalDate.now())
                .verify(true)
                .balance(10000)
                .build();
        given(customerRepository.findById(anyLong()))
                .willReturn(Optional.of(choi));
        CustomerBalanceHistory balanceHistory = CustomerBalanceHistory.builder()
                .id(1L)
                .changeMoney(50000)
                .currentMoney(60000)
                .description("적립금 충전")
                .fromMessage("kim")
                .customer(choi)
                .build();
        given(customerBalanceHistoryRepository.findFirstByCustomerIdOrderByIdDesc(anyLong()))
                .willReturn(Optional.of(balanceHistory));
        given(customerBalanceHistoryRepository.save(any()))
                .willReturn(balanceHistory);

        ChangeBalanceForm form = ChangeBalanceForm.builder()
                .from("kim")
                .message("적립금 충전")
                .money(50000)
                .build();
        ArgumentCaptor<CustomerBalanceHistory> captor = ArgumentCaptor.forClass(CustomerBalanceHistory.class);

        // when
        CustomerBalanceHistory customerBalanceHistory = customerBalanceService.changeBalance(1L, form);

        // then
        verify(customerBalanceHistoryRepository, times(1)).save(captor.capture());
        assertEquals(50000, captor.getValue().getChangeMoney());
        assertEquals("kim", captor.getValue().getFromMessage());
        assertEquals(60000, customerBalanceHistory.getCurrentMoney());
        assertEquals("적립금 충전", captor.getValue().getDescription());
    }

    @Test
    @DisplayName("고객의 잔액을 변경하는 경우(음수) - 잔액 변경 실패")
    void notEnoughBalanceTest() {
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
        given(customerRepository.findById(anyLong()))
                .willReturn(Optional.of(choi));

        CustomerBalanceHistory balanceHistory = CustomerBalanceHistory.builder()
                .id(1L)
                .currentMoney(10000)
                .description("적립금 사용")
                .fromMessage("kim")
                .customer(choi)
                .build();
        given(customerBalanceHistoryRepository.findFirstByCustomerIdOrderByIdDesc(anyLong()))
                .willReturn(Optional.of(balanceHistory));

        ChangeBalanceForm form = ChangeBalanceForm.builder()
                .from("kim")
                .message("적립금 사용")
                .money(-11000)
                .build();

        // when
        CustomException exception = assertThrows(CustomException.class,
                () -> customerBalanceService.changeBalance(1L, form));


        // then
        assertEquals(NOT_ENOUGH_BALANCE, exception.getErrorCode());
    }
}