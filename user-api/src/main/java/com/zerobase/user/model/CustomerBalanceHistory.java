package com.zerobase.user.model;

import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class CustomerBalanceHistory extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(targetEntity = Customer.class, fetch = FetchType.LAZY)
    private Customer customer;
    private Integer changeMoney;
    private Integer currentMoney;
    private String fromMessage;
    private String description;
}
