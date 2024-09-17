package com.rj.sunbase.Service;

import java.util.Optional;

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
	public Customer updateCustomer(Long id, Customer customer) throws CustomerNotFoundException {
		 return customerRepository.findById(id).map(existingCustomer -> {
	            existingCustomer.setFirstName(customer.getFirstName());
	            existingCustomer.setLastName(customer.getLastName());
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
	public Customer getCustomerById(Long id) throws CustomerNotFoundException {
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
	public void deleteCustomer(Long id) throws CustomerNotFoundException {
		if (!customerRepository.existsById(id)) {
            throw new CustomerNotFoundException("Customer not found with id: " + id);
        }
        customerRepository.deleteById(id);
		
	}

	/**
	 * Synchronize customers from an external API.
	 *
	 * @param token The authorization token to access the external API.
	 * @throws CustomerNotFoundException if there is an error fetching data from the external API.
	 */
	public void syncCustomers(String token) throws CustomerNotFoundException {
		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", "Bearer " + token);

		HttpEntity<String> entity = new HttpEntity<>(headers);
		ResponseEntity<Customer[]> response = restTemplate.getForEntity(EXTERNAL_API_URL, Customer[].class, entity);

		if (response.getStatusCode().is2xxSuccessful()) {
			Customer[] customers = response.getBody();
			if (customers != null) {
				for (Customer customer : customers) {
					Optional<Customer> existingCustomer = customerRepository.findByEmail(customer.getEmail());
					if (existingCustomer.isPresent()) {
						Customer updatedCustomer = existingCustomer.get();
						updatedCustomer.setFirstName(customer.getFirstName());
						updatedCustomer.setLastName(customer.getLastName());
						updatedCustomer.setStreet(customer.getStreet());
						updatedCustomer.setAddress(customer.getAddress());
						updatedCustomer.setCity(customer.getCity());
						updatedCustomer.setState(customer.getState());
						updatedCustomer.setPhone(customer.getPhone());
						customerRepository.save(updatedCustomer);
					} else {
						customerRepository.save(customer);
					}
				}
			}
		} else {
			throw new CustomerNotFoundException("Failed to fetch customer data from external API");
		}
	}


}
