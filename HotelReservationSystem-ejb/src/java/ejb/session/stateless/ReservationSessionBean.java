package ejb.session.stateless;

import entity.Reservation;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Stateless
public class ReservationSessionBean implements ReservationSessionBeanRemote, ReservationSessionBeanLocal {

    @PersistenceContext(unitName = "HotelReservationSystem-ejbPU")
    private EntityManager em;

    // Create a new reservation and return its ID
    public Long createReservation(Reservation reservation) {
        em.persist(reservation);
        em.flush();
        return reservation.getReservationId();
    }

    // Retrieve a specific reservation by ID
    public Reservation retrieveReservationById(Long reservationId) {
        return em.find(Reservation.class, reservationId);
    }

    // Retrieve all reservations
    public List<Reservation> retrieveAllReservations() {
        return em.createQuery("SELECT r FROM Reservation r", Reservation.class).getResultList();
    }

    // Update a reservation's details
    public void updateReservation(Reservation reservation) {
        em.merge(reservation);
    }

    // Delete a reservation by ID
    public void deleteReservation(Long reservationId) {
        Reservation reservation = retrieveReservationById(reservationId);
        if (reservation != null) {
            em.remove(reservation);
        }
    }
}
