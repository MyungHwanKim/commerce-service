package com.zerobase.order.service;

import com.zerobase.order.domain.product.AddProductItemForm;
import com.zerobase.order.domain.product.UpdateProductItemForm;
import com.zerobase.order.exception.CustomException;
import com.zerobase.order.model.Product;
import com.zerobase.order.model.ProductItem;
import com.zerobase.order.repository.ProductItemRepository;
import com.zerobase.order.repository.ProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Optional;

import static com.zerobase.order.exception.ErrorCode.NOT_FOUND_ITEM;
import static com.zerobase.order.exception.ErrorCode.SAME_ITEM_NAME;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class ProductItemServiceTest {

    @Mock
    private ProductRepository productRepository;
    @Mock
    private ProductItemRepository productItemRepository;
    @InjectMocks
    private ProductItemService productItemService;

    @Test
    @DisplayName("상품 아이템을 동록하는 경우(아이템 명 중복) - 상품 아이템 등록 실패")
    void addProductItemTest() {
        // given
        Long sellerId = 1L;
        Product nike = Product.builder()
                .id(1L)
                .sellerId(1L)
                .name("나이키 에어포스")
                .description("상품 등록")
                .productItems(Arrays.asList(
                        ProductItem.builder()
                                .id(1L)
                                .sellerId(1L)
                                .name("270")
                                .price(100000)
                                .count(1)
                                .build(),
                        ProductItem.builder()
                                .id(2L)
                                .sellerId(1L)
                                .name("280")
                                .price(120000)
                                .count(1)
                                .build()
                ))
                .build();
        given(productRepository.findBySellerIdAndId(anyLong(), anyLong()))
                .willReturn(Optional.of(nike));
        AddProductItemForm form = makeProductItemForm(1L, "280");

        // when
        CustomException exception = assertThrows(CustomException.class,
                () -> productItemService.addProductItem(sellerId, form));

        // then
        assertEquals(SAME_ITEM_NAME, exception.getErrorCode());
    }

    private static AddProductItemForm makeProductItemForm(Long productId, String name) {
        return AddProductItemForm.builder()
                .productId(productId)
                .name(name)
                .price(120000)
                .count(1)
                .build();
    }

    @Test
    @DisplayName("상품 아이템을 수정하는 경우 - 상품 아이템 수정 성공")
    void updateProductItemTest() {
        // given
        Long sellerId = 1L;
        ProductItem nikeItem = ProductItem.builder()
                .id(1L)
                .sellerId(1L)
                .name("270")
                .price(100000)
                .count(1)
                .build();
        given(productItemRepository.findById(anyLong())
                .filter(x -> x.getSellerId().equals(sellerId)))
                .willReturn(Optional.of(nikeItem));

        UpdateProductItemForm form = updateProductItemForm(1L, "290");
        // when
        ProductItem productItem = productItemService.updateProductItem(sellerId, form);

        // then
        assertEquals("290", productItem.getName());
        assertEquals(150000, productItem.getPrice());
        assertEquals(3, productItem.getCount());
    }

    @Test
    @DisplayName("상품 아이템을 수정하는 경우(상품 아이템 없음) - 상품 아이템 수정 실패")
    void notFoundItemUpdateTest() {
        // given
        Long sellerId = 2L;
        ProductItem nikeItem = ProductItem.builder()
                .id(1L)
                .sellerId(1L)
                .name("270")
                .price(100000)
                .count(1)
                .build();
        given(productItemRepository.findById(anyLong())
                .filter(x -> x.getSellerId().equals(sellerId)))
                .willReturn(Optional.of(nikeItem));
        UpdateProductItemForm form = updateProductItemForm(1L, "290");

        // when
        CustomException exception = assertThrows(CustomException.class,
                () -> productItemService.updateProductItem(sellerId, form));

        // then
        assertEquals(NOT_FOUND_ITEM, exception.getErrorCode());
    }

    private static UpdateProductItemForm updateProductItemForm(Long productId, String name) {
        return UpdateProductItemForm.builder()
                .id(productId)
                .name(name)
                .price(150000)
                .count(3)
                .build();
    }

    @Test
    @DisplayName("상품 아이템을 삭제하는 경우 - 상품 아이템 삭제 성공")
    void deleteProductItemTest() {
        // given
        Long sellerId = 1L;
        ProductItem nikeItem = ProductItem.builder()
                .id(1L)
                .sellerId(1L)
                .name("270")
                .price(100000)
                .count(1)
                .build();
        given(productItemRepository.findById(anyLong())
                .filter(x -> x.getSellerId().equals(sellerId)))
                .willReturn(Optional.of(nikeItem));

        // when
        productItemService.deleteProductItem(sellerId, nikeItem.getId());
        // then
    }

    @Test
    @DisplayName("상품 아이템을 삭제하는 경우(상품 아이템 없음) - 상품 아이템 삭제 실패")
    void notFoundItemDeleteTest() {
        // given
        Long sellerId = 2L;
        ProductItem nikeItem = ProductItem.builder()
                .id(1L)
                .sellerId(1L)
                .name("270")
                .price(100000)
                .count(1)
                .build();
        given(productItemRepository.findById(anyLong())
                .filter(x -> x.getSellerId().equals(sellerId)))
                .willReturn(Optional.of(nikeItem));

        // when
        CustomException exception = assertThrows(CustomException.class,
                () -> productItemService.deleteProductItem(sellerId, nikeItem.getId()));

        // then
        assertEquals(NOT_FOUND_ITEM, exception.getErrorCode());
    }
}