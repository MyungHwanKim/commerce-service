package com.zerobase.order.domain.product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddProductCartForm {

    private Long id;
    private Long sellerId;
    private String name;
    private String description;
    private List<ProductItem> items;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ProductItem {
        private Long id;
        private String name;
        private Integer count;
        private Integer price;
    }
}
