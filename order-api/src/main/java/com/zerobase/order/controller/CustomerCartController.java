package com.zerobase.order.controller;

import com.zerobase.domain.config.JwtAuthenticationProvider;
import com.zerobase.order.application.CartCustomerApplication;
import com.zerobase.order.application.OrderCustomerApplication;
import com.zerobase.order.domain.product.AddProductCartForm;
import com.zerobase.order.domain.redis.Cart;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/customer/cart")
@RequiredArgsConstructor
public class CustomerCartController {

    private final CartCustomerApplication cartCustomerApplication;
    private final OrderCustomerApplication orderCustomerApplication;
    private final JwtAuthenticationProvider provider;

    @PostMapping
    public ResponseEntity<Cart> addCart(@RequestHeader(name = "X-AUTH-TOKEN") String token,
                                        @RequestBody AddProductCartForm form) {
        return ResponseEntity.ok(cartCustomerApplication.addCart(provider.getUserVo(token).getId(), form));
    }

    @GetMapping
    public ResponseEntity<Cart> showCart(@RequestHeader(name = "X-AUTH-TOKEN") String token) {
        return ResponseEntity.ok(cartCustomerApplication.getCart(provider.getUserVo(token).getId()));
    }

    @PutMapping
    public ResponseEntity<Cart> updateCart(@RequestHeader(name = "X-AUTH-TOKEN") String token,
                                           @RequestBody Cart cart) {
        return ResponseEntity.ok(cartCustomerApplication.updateCart(provider.getUserVo(token).getId(), cart));
    }

    @PostMapping("/order")
    public ResponseEntity<Cart> order(@RequestHeader(name = "X-AUTH-TOKEN") String token,
                                      @RequestBody Cart cart) {
        orderCustomerApplication.order(token, cart);
        return ResponseEntity.ok().build();
    }
}
