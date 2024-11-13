package ejb.session.stateless;

import entity.Partner;
import entity.Reservation;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

@Stateless
public class PartnerSessionBean implements PartnerSessionBeanRemote, PartnerSessionBeanLocal {

    @PersistenceContext(unitName = "HotelReservationSystem-ejbPU")
    private EntityManager em;

    @Override
    public void registerPartner(Partner partner) {
        if (isUsernameUnique(partner.getUsername())) {
            partner.setPassword(hashPassword(partner.getPassword())); // Hash the password before persisting
            em.persist(partner);
        } else {
            throw new IllegalArgumentException("Username is already in use.");
        }
    }

    @Override
    public Long createPartner(Partner partner) {
        partner.setPassword(hashPassword(partner.getPassword())); // Hash the password before persisting
        em.persist(partner);
        em.flush();
        return partner.getPartnerId();
    }

    @Override
    public List<Partner> getAllPartners() {
        return em.createQuery("SELECT p FROM Partner p", Partner.class).getResultList();
    }

    @Override
    public Partner partnerLogin(String username, String password) {
        Partner partner = retrievePartnerByUsername(username);
        if (partner != null && partner.getPassword().equals(hashPassword(password))) {
            return partner;
        } else {
            return null; // Or throw an exception to indicate invalid login
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
    public Partner retrievePartnerById(Long partnerId) {
        return em.find(Partner.class, partnerId);
    }

    @Override
    public Partner retrievePartnerByUsername(String username) {
        Query query = em.createQuery("SELECT p FROM Partner p WHERE p.username = :username", Partner.class);
        query.setParameter("username", username);
        try {
            return (Partner) query.getSingleResult();
        } catch (NoResultException ex) {
            return null;
        }
    }

    @Override
    public void updatePartnerDetails(Partner partner) {
        em.merge(partner); // Update partner details
    }

    @Override
    public void updatePartner(Partner partner) {
        em.merge(partner);
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
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = md.digest(password.getBytes());
            return Base64.getEncoder().encodeToString(hashedBytes);
        } catch (NoSuchAlgorithmException ex) {
            throw new RuntimeException("Error hashing password", ex);
        }
    }
}
