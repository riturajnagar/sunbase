package com.rj.sunbase.Service;

import com.rj.sunbase.Exception.CustomerNotFoundException;
import com.rj.sunbase.Model.Customer;
import com.rj.sunbase.Repository.CustomerRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomerServiceImpl implements CustomerService {

    @Autowired
    private CustomerRepo customerRepo;

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

        if (!customerRepo.existsById(id)) {
            throw new CustomerNotFoundException("Customer not found with id: " + id);
        }
        else{
            customerRepo.deleteById(id);
        }
    }
}
