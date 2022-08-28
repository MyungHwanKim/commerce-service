package com.zerobase.user.repository;

import com.zerobase.user.model.Seller;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SellerRepository extends JpaRepository<Seller, Long> {

    Optional<Seller> findByIdAndEmail(Long id, String email);

    Optional<Seller> findByEmail(String email);
}
