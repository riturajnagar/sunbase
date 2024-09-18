package com.rj.sunbase.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rj.sunbase.Model.Customer;
import com.rj.sunbase.Repository.CustomerRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
public class SyncService {

    @Autowired
    private CustomerRepository customerRepository;

    public String proxyAuthToken() {
        final String STATIC_LOGIN_ID = "test@sunbasedata.com";
        final String STATIC_PASSWORD = "Test@123";
        String url = "https://qa.sunbasedata.com/sunbase/portal/api/assignment_auth.jsp";

        RestTemplate restTemplate = new RestTemplate();

        String jsonPayload = String.format("{\"login_id\":\"%s\",\"password\":\"%s\"}",
                STATIC_LOGIN_ID, STATIC_PASSWORD);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");

        HttpEntity<?> requestEntity = new HttpEntity<>(jsonPayload, headers);

        log.info("REQUEST " + requestEntity.toString());

        ResponseEntity<String> responseEntity = restTemplate.exchange(
                url,
                HttpMethod.POST,
                requestEntity,
                String.class
        );

        log.info("RESULT " + responseEntity.getBody().toString());

        String token = extractAccessToken(responseEntity.getBody());

        log.info("Extracted Token: " + token);

        return token;
    }

    private String extractAccessToken(String responseBody){
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(responseBody);
            return rootNode.path("access_token").asText();
        }
        catch (Exception e) {
            log.error("Error parsing JSON: {}", e.getMessage());
            throw new RuntimeException("Error parsing", e);
        }
    }


    public List<Customer> extractCustomerList(){

        String token = proxyAuthToken();

        log.info("Token: " + token);

        String url =  "https://qa.sunbasedata.com/sunbase/portal/api/assignment.jsp";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("cmd", "get_customer_list");

        HttpEntity<Void> request = new HttpEntity<>(headers);

        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<Customer[]> response = restTemplate.exchange(builder.toUriString(),
                HttpMethod.GET,
                request, Customer[].class
        );

        log.info("Customer List from API: " + Arrays.toString(response.getBody()));

        return Arrays.asList(response.getBody());
    }

    public List<Customer> syncCustomerListFromApi(){

        List<Customer> customerList = extractCustomerList();

        List<Customer> customerListFromDB = customerRepository.findAll();

        log.info("CustomerList "+ customerList.toString());

        //Logic to save the customer list to the database
        for (Customer customer : customerList) {
            boolean exists = customerListFromDB.stream()
                    .anyMatch(dbCustomer -> dbCustomer.getUuid().equals(customer.getUuid()));

            if (exists) {
                throw new RuntimeException("Customer with ID " + customer.getUuid() + " already exists in the database.");
            } else {
                customerRepository.save(customer);
            }
        }

        return customerList;
    }

}
