package ejb.session.stateless;

import entity.Partner;
import entity.Reservation;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

@Stateless
public class PartnerSessionBean implements PartnerSessionBeanRemote, PartnerSessionBeanLocal {

    @PersistenceContext(unitName = "HotelReservationSystem-ejbPU")
    private EntityManager em;
    
    @Override
    public void registerPartner(Partner partner) {
        if (isUsernameUnique(partner.getUsername())) {
            em.persist(partner);
        } else {
            throw new IllegalArgumentException("Username is already in use.");
        }
    }
    
    @Override
    public List<Partner> getAllPartners() {
        return em.createQuery("SELECT p FROM Partner p", Partner.class).getResultList();
    }

    @Override
    public Partner loginPartner(String username, String password) {
        try {
            String passwordHash = hashPassword(password); // Replace with your hash function
            return em.createQuery("SELECT p FROM Partner p WHERE p.username = :username AND p.password = :passwordHash", Partner.class)
                     .setParameter("username", username)
                     .setParameter("passwordHash", passwordHash)
                     .getSingleResult();
        } catch (Exception e) {
            return null; // Handle exceptions as needed
        }
    }

    @Override
    public List<Reservation> viewPartnerReservations(Long partnerId) {
        return em.createQuery("SELECT r FROM Reservation r WHERE r.partner.partnerId = :partnerId", Reservation.class)
                 .setParameter("partnerId", partnerId)
                 .getResultList();
    }

    @Override
    public Partner findPartnerById(Long partnerId) {
        return em.find(Partner.class, partnerId);
    }

    @Override
    public void updatePartnerDetails(Partner partner) {
        em.merge(partner); // Update partner details
    }

    @Override
    public void deletePartner(Long partnerId) {
        Partner partner = findPartnerById(partnerId);
        if (partner != null) {
            em.remove(partner);
        }
    }

    private boolean isUsernameUnique(String username) {
        Query query = em.createQuery("SELECT COUNT(p) FROM Partner p WHERE p.username = :username");
        query.setParameter("username", username);
        Long count = (Long) query.getSingleResult();
        return count == 0;
    }
    
    private String hashPassword(String password) {
        // Implement password hashing here (e.g., using BCrypt or SHA-256)
        // Placeholder implementation for demonstration purposes
        return Integer.toHexString(password.hashCode());
    }
}
