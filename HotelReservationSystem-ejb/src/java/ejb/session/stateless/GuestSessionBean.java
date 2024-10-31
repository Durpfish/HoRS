/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/StatelessEjbClass.java to edit this template
 */
package ejb.session.stateless;

import entity.Guest;
import entity.Reservation;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author josalyn
 */
@Stateless
public class GuestSessionBean implements GuestSessionBeanRemote, GuestSessionBeanLocal {
    
    @PersistenceContext(unitName = "HotelReservationSystem-ejbPU")
    private EntityManager em;

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
    
    public void registerGuest(Guest guest) {
        em.persist(guest); // Save the guest to the database
    }

    public Guest loginGuest(String username, String password) {
        try {
            return em.createQuery("SELECT g FROM Guest g WHERE g.username = :username AND g.password = :password", Guest.class)
                     .setParameter("username", username)
                     .setParameter("password", password)
                     .getSingleResult();
        } catch (Exception e) {
            return null; // Handle exceptions as needed
        }
    }

    public List<Reservation> viewReservations(Long guestId) {
        return em.createQuery("SELECT r FROM Reservation r WHERE r.guest.id = :guestId", Reservation.class)
                 .setParameter("guestId", guestId)
                 .getResultList();
    }

    public void makeReservation(Long guestId, Reservation reservationDetails) {
        Guest guest = em.find(Guest.class, guestId);
        reservationDetails.setGuest(guest); // Associate reservation with guest
        em.persist(reservationDetails);     // Save reservation to the database
    }
}
