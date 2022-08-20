package com.zerobase.user.domain.customer;

import com.zerobase.user.model.Customer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CustomerDto {

    private Long id;
    private String email;

    public static CustomerDto from(Customer customer) {
        return new CustomerDto(customer.getId(), customer.getEmail());
    }
}
