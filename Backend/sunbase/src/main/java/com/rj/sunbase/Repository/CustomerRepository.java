package com.rj.sunbase.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.rj.sunbase.Model.Customer;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, String> {

	Optional<Customer> findByEmail(String email);

}
