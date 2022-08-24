package com.zerobase.order.repository;

import com.zerobase.order.model.Product;

import java.util.List;

public interface ProductRepositoryCustom {

    List<Product> searchByName(String name);
}
