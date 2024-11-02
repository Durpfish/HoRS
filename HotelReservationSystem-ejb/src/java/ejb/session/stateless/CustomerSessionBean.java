package ejb.session.stateless;

import entity.Customer;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

@Stateless
public class CustomerSessionBean implements CustomerSessionBeanRemote, CustomerSessionBeanLocal {

    @PersistenceContext(unitName = "HotelReservationSystem-ejbPU")
    private EntityManager em;
    
    @Override
    public void createCustomerAccount(Customer customer) {
        if (isEmailUnique(customer.getEmail())) {
            em.persist(customer);
        } else {
            throw new IllegalArgumentException("Email is already in use.");
        }
    }

    @Override
    public void updateCustomerDetails(Customer customer) {
        if (isEmailUnique(customer.getEmail()) || isUpdatingOwnAccount(customer)) {
            em.merge(customer);
        } else {
            throw new IllegalArgumentException("Email is already in use by another account.");
        }
    }

    @Override
    public Customer findCustomerByGuestId(Long guestId) {
        return em.find(Customer.class, guestId);
    }

    @Override
    public Customer findCustomerByEmail(String email) {
        Query query = em.createQuery("SELECT c FROM Customer c WHERE c.email = :email");
        query.setParameter("email", email);
        return (Customer) query.getSingleResult();
    }

    @Override
    public void deleteCustomer(Long guestId) {
        Customer customer = findCustomerByGuestId(guestId);
        if (customer != null) {
            em.remove(customer);
        }
    }
    
    private boolean isEmailUnique(String email) {
        Query query = em.createQuery("SELECT COUNT(c) FROM Customer c WHERE c.email = :email");
        query.setParameter("email", email);
        Long count = (Long) query.getSingleResult();
        return count == 0;
    }
    
    private boolean isUpdatingOwnAccount(Customer customer) {
        Customer existingCustomer = findCustomerByGuestId(customer.getGuestId());
        return existingCustomer != null && existingCustomer.getEmail().equals(customer.getEmail());
    }
}
