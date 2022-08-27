package com.zerobase.order.application;

import com.zerobase.order.client.UserClient;
import com.zerobase.order.client.user.ChangeBalanceForm;
import com.zerobase.order.client.user.CustomerDto;
import com.zerobase.order.domain.redis.Cart;
import com.zerobase.order.exception.CustomException;
import com.zerobase.order.model.ProductItem;
import com.zerobase.order.service.ProductItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.IntStream;

import static com.zerobase.order.exception.ErrorCode.ORDER_FAIL_CHECK_CART;
import static com.zerobase.order.exception.ErrorCode.ORDER_FAIL_NO_MONEY;

@Service
@RequiredArgsConstructor
public class OrderCustomerApplication {

    private final CartCustomerApplication cartCustomerApplication;
    private final UserClient userClient;
    private final ProductItemService productItemService;

    @Transactional
    public void order(String token, Cart cart) {
        Cart orderCart = cartCustomerApplication.refreshCart(cart);
        if (orderCart.getMessages().size() > 0) {
            throw new CustomException(ORDER_FAIL_CHECK_CART);
        }
        CustomerDto customerDto = userClient.getCustomerInfo(token).getBody();

        int totalCount = getTotalPrice(cart);
        if (customerDto.getBalance() < totalCount) {
            throw new CustomException(ORDER_FAIL_NO_MONEY);
        }

        userClient.changeBalance(token, ChangeBalanceForm.builder()
                        .from("USER")
                        .message("Order")
                        .money(-totalCount)
                        .build());

        for (Cart.Product product : orderCart.getProducts()) {
            for (Cart.ProductItem cartItem : product.getItems()) {
                ProductItem productItem = productItemService.getProductItem(cartItem.getId());
                productItem.setCount(productItem.getCount() - cartItem.getCount());
            }
        }
    }

    private Integer getTotalPrice(Cart cart) {
        return cart.getProducts().stream()
                .flatMapToInt(product -> product.getItems().stream()
                        .flatMapToInt(productItem -> IntStream.of(productItem.getPrice() + productItem.getCount())))
                .sum();
    }
}
