package ejb.session.stateless;

import entity.Guest;
import entity.Reservation;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

@Stateless
public class GuestSessionBean implements GuestSessionBeanRemote, GuestSessionBeanLocal {
    
    @PersistenceContext(unitName = "HotelReservationSystem-ejbPU")
    private EntityManager em;

    @Override
    public void registerGuest(Guest guest) {
        if (isUsernameUnique(guest.getUsername())) {
            em.persist(guest); // Save the guest to the database
        } else {
            throw new IllegalArgumentException("Username is already in use.");
        }
    }

    @Override
    public Guest loginGuest(String username, String password) {
        try {
            // Assuming password is hashed; replace "passwordHash" with the hashed password
            String passwordHash = hashPassword(password);
            return em.createQuery("SELECT g FROM Guest g WHERE g.username = :username AND g.password = :passwordHash", Guest.class)
                     .setParameter("username", username)
                     .setParameter("passwordHash", passwordHash)
                     .getSingleResult();
        } catch (Exception e) {
            return null; // Handle exceptions as needed
        }
    }

    @Override
    public List<Reservation> viewReservations(Long guestId) {
        return em.createQuery("SELECT r FROM Reservation r WHERE r.guest.guestId = :guestId", Reservation.class)
                 .setParameter("guestId", guestId)
                 .getResultList();
    }

    @Override
    public void makeReservation(Long guestId, Reservation reservationDetails) {
        Guest guest = em.find(Guest.class, guestId);
        reservationDetails.setGuest(guest); // Associate reservation with guest
        em.persist(reservationDetails);     // Save reservation to the database
    }

    @Override
    public Guest findGuestByUsername(String username) {
        return em.createQuery("SELECT g FROM Guest g WHERE g.username = :username", Guest.class)
                 .setParameter("username", username)
                 .getSingleResult();
    }
    
    public Guest findGuestById(Long guestId) {
        return em.find(Guest.class, guestId);
    }
    
    public Reservation viewReservationDetails(Long reservationId) {
        return em.find(Reservation.class, reservationId);
    }



    @Override
    public void updateGuestDetails(Guest guest) {
        em.merge(guest); // Update guest details
    }

    @Override
    public void deleteGuest(Long guestId) {
        Guest guest = em.find(Guest.class, guestId);
        if (guest != null) {
            em.remove(guest);
        }
    }

    private boolean isUsernameUnique(String username) {
        Query query = em.createQuery("SELECT COUNT(g) FROM Guest g WHERE g.username = :username");
        query.setParameter("username", username);
        Long count = (Long) query.getSingleResult();
        return count == 0;
    }
    
    private String hashPassword(String password) {
        // Implement password hashing here (e.g., using a library like BCrypt or SHA-256)
        // Placeholder implementation for demonstration purposes
        return Integer.toHexString(password.hashCode());
    }
}
