package com.zerobase.user.repository;

import com.zerobase.user.model.CustomerBalanceHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Repository
public interface CustomerBalanceHistoryRepository extends JpaRepository<CustomerBalanceHistory, Long> {

    Optional<CustomerBalanceHistory> findFirstByCustomerIdOrderByIdDesc(@RequestParam("customer_id") Long customerId);
}
