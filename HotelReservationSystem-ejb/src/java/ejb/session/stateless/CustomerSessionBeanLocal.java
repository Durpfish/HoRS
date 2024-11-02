/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/SessionLocal.java to edit this template
 */
package ejb.session.stateless;

import entity.Customer;
import javax.ejb.Local;

/**
 *
 * @author josalyn
 */
@Local
public interface CustomerSessionBeanLocal {

    public void createCustomerAccount(Customer customer);

    public void updateCustomerDetails(Customer customer);

    public Customer findCustomerByEmail(String email);

    public void deleteCustomer(Long customerId);

    public Customer findCustomerByGuestId(Long guestId);
    
}
