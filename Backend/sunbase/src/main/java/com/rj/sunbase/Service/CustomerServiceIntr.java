package com.rj.sunbase.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.rj.sunbase.Exception.CustomerNotFoundException;
import com.rj.sunbase.Model.Customer;

public interface CustomerServiceIntr {
	
	public Customer createCustomer(Customer customer) throws CustomerNotFoundException;
	public Customer updateCustomer(Long id, Customer customer) throws CustomerNotFoundException;
	public Page<Customer> getCustomers(Pageable pageable);
	public Customer getCustomerById(Long id) throws CustomerNotFoundException;
    public void deleteCustomer(Long id) throws CustomerNotFoundException;
    //public void saveOrUpdateCustomers(List<Customer> customers) throws CustomerNotFoundException;
    public void syncCustomers(String token) throws CustomerNotFoundException;
}
