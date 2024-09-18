package com.rj.sunbase.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.rj.sunbase.Exception.CustomerNotFoundException;
import com.rj.sunbase.Model.Customer;
import com.rj.sunbase.Repository.CustomerRepository;

@Service
public class CustomerServiceImpl implements CustomerServiceIntr{
	
	@Autowired
	private CustomerRepository customerRepository;

	private final RestTemplate restTemplate = new RestTemplate();
	private final String EXTERNAL_API_URL = "https://qa.sunbasedata.com/sunbase/portal/api/assignment.jsp?cmd=get_customer_list";


	/**
     * Create a new customer or update an existing customer.
     * 
     * @param customer The customer to be created or updated.
     * @return The created or updated customer.
     * @throws CustomerNotFoundException if the customer is null.
     */
	@Override
	public Customer createCustomer(Customer customer) throws CustomerNotFoundException {
		
		if(customer == null) {
			throw new CustomerNotFoundException("Customer Not found");
		}
		customer.setUuid(""+UUID.randomUUID());
		return customerRepository.save(customer);
	}

    /**
     * Update an existing customer by ID.
     * 
     * @param id The ID of the customer to be updated.
     * @param customer The customer data to update.
     * @return The updated customer.
     * @throws CustomerNotFoundException if no customer is found with the given ID.
     */
	@Override
	public Customer updateCustomer(String id, Customer customer) throws CustomerNotFoundException {
		 return customerRepository.findById(id).map(existingCustomer -> {
	            existingCustomer.setFirst_name(customer.getFirst_name());
	            existingCustomer.setLast_name(customer.getLast_name());
	            existingCustomer.setStreet(customer.getStreet());
	            existingCustomer.setAddress(customer.getAddress());
	            existingCustomer.setCity(customer.getCity());
	            existingCustomer.setState(customer.getState());
	            existingCustomer.setEmail(customer.getEmail());
	            existingCustomer.setPhone(customer.getPhone());
	            return customerRepository.save(existingCustomer);
	        }).orElseThrow(() -> new CustomerNotFoundException("Customer not found with id: " + id));
	    
	}
	
	 /**
     * Retrieve a paginated list of customers.
     * 
     * @param pageable The pagination and sorting information.
     * @return A page of customers.
     */
	@Override
	public Page<Customer> getCustomers(Pageable pageable) {
		 return customerRepository.findAll(pageable);
	}

	 /**
     * Retrieve a customer by ID.
     * 
     * @param id The ID of the customer to retrieve.
     * @return The customer with the given ID.
     * @throws CustomerNotFoundException if no customer is found with the given ID.
     */
	@Override
	public Customer getCustomerById(String id) throws CustomerNotFoundException {
		return customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found with id: " + id));
	}
	
	/**
     * Delete a customer by ID.
     * 
     * @param id The ID of the customer to delete.
     * @throws CustomerNotFoundException if no customer is found with the given ID.
     */
	@Override
	public void deleteCustomer(String id) throws CustomerNotFoundException {
		if (!customerRepository.existsById(id)) {
            throw new CustomerNotFoundException("Customer not found with id: " + id);
        }
        customerRepository.deleteById(id);
		
	}

	@Override
	public List<Customer> getCustomers() throws CustomerNotFoundException {
		List<Customer> customers = customerRepository.findAll();
		if(customers.size() == 0) throw new CustomerNotFoundException("No Customers Available");
		return customers;
	}
}
