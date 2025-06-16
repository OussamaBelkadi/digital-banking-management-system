package com.example.securityjwt.repositories;

import com.example.securityjwt.entity.Customer;
import org.hibernate.query.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.awt.print.Pageable;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    @Query("SELECT c FROM Customer c WHERE c.name LIKE %:keyword% OR c.email LIKE %:keyword%")
    Page<Customer> findByKeyword(@Param("keyword") String keyword, Pageable pageable);

    boolean existsByEmail(String email);
}
