package lk.saumiz.banking.bo;

import lk.saumiz.banking.dto.CustomerDTO;
import lk.saumiz.banking.entity.Customer;
import java.util.List;

public interface CustomerBO {
    String createCustomer(CustomerDTO dto) throws Exception;
    boolean updateCustomer(CustomerDTO dto) throws Exception;
    boolean deleteCustomer(String customerId) throws Exception;
    Customer getCustomer(String customerId) throws Exception;
    List<Customer> getAllCustomers() throws Exception;
    List<Customer> searchCustomers(String keyword) throws Exception;
}
