package com.zerobase.user.service.seller;

import com.zerobase.user.model.Seller;
import com.zerobase.user.repository.SellerRepository;
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
class SellerServiceTest {

    @Mock
    private SellerRepository sellerRepository;

    @InjectMocks
    private SellerService sellerService;

    @Test
    @DisplayName("로그인 토큰을 위한 인증을 확인할 경우(셀러) - 로그인 인증 성공")
    void findValidCustomerTest() {
        // given
        Seller choi = Seller.builder()
                .id(1L)
                .email("kmh9250@naver.com")
                .name("choi")
                .password("1234")
                .phone("01012341234")
                .birth(LocalDate.now())
                .verify(true)
                .build();
        given(sellerRepository.findByEmail(anyString()))
                .willReturn(Optional.of(choi));

        // when
        Optional<Seller> seller = sellerService.findValidSeller("kmh9250@naver.com", "1234");


        // then
        assertEquals(choi.getEmail(), seller.get().getEmail());
        assertEquals(choi.getPassword(), seller.get().getPassword());
        assertTrue(seller.get().isVerify());
    }

    @Test
    @DisplayName("로그인 토큰을 위한 인증을 확인할 경우(셀러) - 로그인 인증 실패")
    void loginCheckFailTest() {
        // given
        Seller choi = Seller.builder()
                .id(3L)
                .email("abc@naver.com")
                .name("choi")
                .password("1234")
                .phone("01012341234")
                .birth(LocalDate.now())
                .verify(false)
                .build();
        given(sellerRepository.findByEmail(anyString()))
                .willReturn(Optional.of(choi));

        // when
        Optional<Seller> validSeller1 = sellerService.findValidSeller("abc1234@gmail.com", "1234");
        Optional<Seller> validSeller2 = sellerService.findValidSeller("abc@naver.com", "5678");
        Optional<Seller> validSeller3 = sellerService.findValidSeller("abc@naver.com", "1234");

        // then
        assertThat(validSeller1).isEmpty();
        assertThat(validSeller2).isEmpty();
        assertThat(validSeller3).isEmpty();
    }
}