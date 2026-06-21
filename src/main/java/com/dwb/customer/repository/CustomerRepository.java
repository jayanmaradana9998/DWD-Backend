package com.dwb.customer.repository;

import com.dwb.customer.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    boolean existsByPhoneAndRetailerProfile_Id(String phone, Long retailerProfileId);

    Optional<Customer> findByIdAndRetailerProfile_Id(Long id, Long retailerProfileId);

    long countByRetailerProfile_Id(Long retailerProfileId);

    // Partial phone match — used for type-ahead search (min 5 chars from frontend)
    @Query("SELECT c FROM Customer c WHERE c.retailerProfile.id = :retailerId AND c.phone LIKE %:q%")
    List<Customer> searchByPhone(@Param("retailerId") Long retailerId, @Param("q") String q);

    // Partial email match
    @Query("SELECT c FROM Customer c WHERE c.retailerProfile.id = :retailerId AND LOWER(c.email) LIKE LOWER(CONCAT('%', :q, '%'))")
    List<Customer> searchByEmail(@Param("retailerId") Long retailerId, @Param("q") String q);
}
