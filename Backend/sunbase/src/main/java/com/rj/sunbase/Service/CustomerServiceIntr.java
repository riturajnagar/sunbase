package com.rj.sunbase.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.rj.sunbase.Exception.CustomerNotFoundException;
import com.rj.sunbase.Model.Customer;

import java.util.List;

public interface CustomerServiceIntr {
	
	public Customer createCustomer(Customer customer) throws CustomerNotFoundException;
	public Customer updateCustomer(String id, Customer customer) throws CustomerNotFoundException;
	public Page<Customer> getCustomers(Pageable pageable);
	public Customer getCustomerById(String id) throws CustomerNotFoundException;
    public void deleteCustomer(String id) throws CustomerNotFoundException;
	public List<Customer> getCustomers() throws CustomerNotFoundException;
}
