package com.zerobase.order.service;

import com.zerobase.order.domain.product.AddProductForm;
import com.zerobase.order.domain.product.AddProductItemForm;
import com.zerobase.order.domain.product.UpdateProductForm;
import com.zerobase.order.domain.product.UpdateProductItemForm;
import com.zerobase.order.exception.CustomException;
import com.zerobase.order.model.Product;
import com.zerobase.order.model.ProductItem;
import com.zerobase.order.repository.ProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.zerobase.order.exception.ErrorCode.NOT_FOUND_PRODUCT;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;
    @InjectMocks
    private ProductService productService;

    @Test
    @DisplayName("상품을 등록하는 경우 - 상품 등록 성공")
    void addProductTest() {
        // given
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
                                .build()
                ))
                .build();
        given(productRepository.save(any()))
                .willReturn(nike);

        // when
        Product result = productService.addProduct(1L, makeProductForm("나이키 에어포스", "상품 등록", 1));

        // then
        assertNotNull(result);
        assertEquals(result.getName(), "나이키 에어포스");
        assertEquals(result.getDescription(), "상품 등록");
        assertEquals(result.getProductItems().size(), 1);
        assertEquals(result.getProductItems().get(0).getName(), "270");
        assertEquals(result.getProductItems().get(0).getPrice(), 100000);
        assertEquals(result.getProductItems().get(0).getCount(), 1);
    }


    private static AddProductForm makeProductForm(String name, String description, int itemCount) {
        List<AddProductItemForm> itemForms = new ArrayList<>();
        for (int i = 0; i < itemCount; i++) {
            itemForms.add(makeProductItemForm((long) i, "270"));
        }
        return AddProductForm.builder()
                .name(name)
                .description(description)
                .items(itemForms)
                .build();
    }

    private static AddProductItemForm makeProductItemForm(Long productId, String name) {
        return AddProductItemForm.builder()
                .productId(productId)
                .name(name)
                .price(10000)
                .count(1)
                .build();
    }

    @Test
    @DisplayName("상품을 수정하는 경우 - 상품 수정 성공")
    void updateProductTest() {
        // given
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
                                .build()
                ))
                .build();
        Product updateNike = Product.builder()
                .id(1L)
                .sellerId(1L)
                .name("나이키 에어포스 한정판")
                .description("상품 수정")
                .productItems(Arrays.asList(
                        ProductItem.builder()
                                .id(1L)
                                .sellerId(1L)
                                .name("280")
                                .price(120000)
                                .count(1)
                                .build()
                ))
                        .build();
        given(productRepository.findBySellerIdAndId(anyLong(), anyLong()))
                .willReturn(Optional.of(nike))
                .willReturn(Optional.of(updateNike));

        // when
        Product product = productService.updateProduct(1L, updateProductForm("나이키 에어포스 한정판", "상품 수정", 1));
        // then
        assertEquals(product.getName(), "나이키 에어포스 한정판");
        assertEquals(product.getDescription(), "상품 수정");
        assertEquals(product.getProductItems().get(0).getName(), "280");
        assertEquals(product.getProductItems().get(0).getPrice(), 120000);
    }

    @Test
    @DisplayName("상품을 수정하는 경우(상품이 없을 때) - 상품 수정 실패")
    void notFountProductTest() {
        // given
        Product updateNike = Product.builder()
                .id(1L)
                .sellerId(1L)
                .name("나이키 에어포스 한정판")
                .description("상품 수정")
                .productItems(Arrays.asList(
                        ProductItem.builder()
                                .id(1L)
                                .sellerId(1L)
                                .name("280")
                                .price(120000)
                                .count(1)
                                .build()
                ))
                .build();
        given(productRepository.findBySellerIdAndId(anyLong(), anyLong()))
                .willReturn(Optional.empty())
                .willReturn(Optional.of(updateNike));

        UpdateProductForm form = updateProductForm("나이키 에어포스 한정판", "상품 수정", 1);

        // when
        CustomException exception = assertThrows(CustomException.class,
                () -> productService.updateProduct(5L, form));
        // then
        assertEquals(NOT_FOUND_PRODUCT, exception.getErrorCode());
    }

    private static UpdateProductForm updateProductForm(String name, String description, int itemCount) {
        List<UpdateProductItemForm> itemForms = new ArrayList<>();
        for (int i = 1; i <= itemCount; i++) {
            itemForms.add(updateProductItemForm((long) i, "280"));
        }
        return UpdateProductForm.builder()
                .id(1L)
                .name(name)
                .description(description)
                .items(itemForms)
                .build();
    }

    private static UpdateProductItemForm updateProductItemForm(Long id, String name) {
        return UpdateProductItemForm.builder()
                .id(id)
                .name(name)
                .price(120000)
                .count(1)
                .build();
    }

    @Test
    @DisplayName("상품을 삭제하는 경우 - 상품 삭제 성공")
    void deleteProductTest() {
        // given
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
                                .build()
                ))
                .build();
        given(productRepository.findBySellerIdAndId(anyLong(), anyLong()))
                .willReturn(Optional.of(nike))
                .willReturn(Optional.of(nike));

        // when
        productService.deleteProduct(1L, 1L);
        
        // then
    }

    @Test
    @DisplayName("상품을 삭제하는 경우(없는 상품) - 상품 삭제 실패")
    void NotFoundProductDeleteProductTest() {
        // given
        given(productRepository.findBySellerIdAndId(anyLong(), anyLong()))
                .willReturn(Optional.empty())
                .willReturn(Optional.empty());

        // when
        CustomException exception = assertThrows(CustomException.class,
                () -> productService.deleteProduct(1L, 1L));

        // then
        assertEquals(NOT_FOUND_PRODUCT, exception.getErrorCode());
    }


}