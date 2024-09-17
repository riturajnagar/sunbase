package com.rj.sunbase.Service;

import com.rj.sunbase.Exception.CustomerNotFoundException;
import com.rj.sunbase.Model.Customer;
import com.rj.sunbase.Repository.CustomerRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.http.HttpHeaders;
import java.util.Optional;

@Service
public class CustomerServiceImpl implements CustomerService {

    @Autowired
    private CustomerRepo customerRepo;
    private final RestTemplate restTemplate = new RestTemplate();
    private final String EXTERNAL_API_URL = "https://qa.sunbasedata.com/sunbase/portal/api/assignment.jsp?cmd=get_customer_list";


    /**
     * @param customer
     * @return The created or updated customer.
     * @throws CustomerNotFoundException if the customer is null.
     */
    @Override
    public Customer createCustomer(Customer customer) throws CustomerNotFoundException {

        if(customer == null) throw new CustomerNotFoundException("Enter Customer Details to Register Customer");

        return customerRepo.save(customer);
    }

    /**
     * @param id
     * @param customer
     * @return The updated customer.
     * @throws CustomerNotFoundException if no customer is found with the given ID.
     */
    @Override
    public Customer updateCustomer(Long id, Customer customer) throws CustomerNotFoundException {

        Optional<Customer> data = customerRepo.findById(id);

        return data.map(existingCustomer -> {
            existingCustomer.setFirstName(customer.getFirstName());
            existingCustomer.setLastName(customer.getLastName());
            existingCustomer.setStreet(customer.getStreet());
            existingCustomer.setAddress(customer.getAddress());
            existingCustomer.setCity(customer.getCity());
            existingCustomer.setState(customer.getState());
            existingCustomer.setEmail(customer.getEmail());
            existingCustomer.setPhone(customer.getPhone());
            return customerRepo.save(existingCustomer);
        }).orElseThrow(()-> new CustomerNotFoundException("Customer Not Exists"));

    }

    /**
     * @param pageable The pagination information.
     * @return A page of customers.
     */
    @Override
    public Page<Customer> getCustomers(Pageable pageable) {
        return customerRepo.findAll(pageable);
    }

    /**
     * @param id
     * @return The customer with the given ID.
     * @throws CustomerNotFoundException if no customer is found with the given ID.
     */
    @Override
    public Customer getCustomerById(Long id) throws CustomerNotFoundException {
        Customer customer = null;

        Optional<Customer> data = customerRepo.findById(id);

        if(data.isPresent()){
            customer = data.get();
        }else{
            throw new CustomerNotFoundException("Customer not exists");
        }

        return customer;
    }

    /**
     * @param id
     * @throws CustomerNotFoundException if no customer is found with the given ID.
     */
    @Override
    public void deleteCustomer(Long id) throws CustomerNotFoundException {

        if (!customerRepo.existsById(id))
            throw new CustomerNotFoundException("Customer not found with id: " + id);

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
                    Optional<Customer> existingCustomer = customerRepo.findByEmail(customer.getEmail());
                    if (existingCustomer.isPresent()) {
                        Customer updatedCustomer = existingCustomer.get();
                        updatedCustomer.setFirstName(customer.getFirstName());
                        updatedCustomer.setLastName(customer.getLastName());
                        updatedCustomer.setStreet(customer.getStreet());
                        updatedCustomer.setAddress(customer.getAddress());
                        updatedCustomer.setCity(customer.getCity());
                        updatedCustomer.setState(customer.getState());
                        updatedCustomer.setPhone(customer.getPhone());
                        customerRepo.save(updatedCustomer);
                    } else {
                        customerRepo.save(customer);
                    }
                }
            }
        } else {
            throw new CustomerNotFoundException("Failed to fetch customer data from external API");
        }
    }
}
