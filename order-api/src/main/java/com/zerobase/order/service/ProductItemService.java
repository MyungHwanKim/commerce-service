package com.zerobase.order.service;

import com.zerobase.order.domain.product.AddProductItemForm;
import com.zerobase.order.exception.CustomException;
import com.zerobase.order.model.Product;
import com.zerobase.order.model.ProductItem;
import com.zerobase.order.repository.ProductItemRepository;
import com.zerobase.order.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.zerobase.order.exception.ErrorCode.NOT_FOUND_PRODUCT;
import static com.zerobase.order.exception.ErrorCode.SAME_ITEM_NAME;

@Service
@RequiredArgsConstructor
public class ProductItemService {

    private final ProductRepository productRepository;
    private final ProductItemRepository productItemRepository;

    @Transactional
    public Product addProductItem(Long sellerId, AddProductItemForm form) {
        Product product = productRepository.findBySellerIdAndId(sellerId, form.getProductId())
                .orElseThrow(() -> new CustomException(NOT_FOUND_PRODUCT));
        if (product.getProductItems().stream()
                .anyMatch(item -> item.getName().equals(form.getName()))) {
            throw new CustomException(SAME_ITEM_NAME);
        }
        ProductItem productItem = ProductItem.of(sellerId, form);
        product.getProductItems().add(productItem);
        return product;
    }
}
