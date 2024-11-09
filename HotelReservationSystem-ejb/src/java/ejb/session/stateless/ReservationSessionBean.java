package ejb.session.stateless;

import entity.Reservation;
import entity.RoomAllocationExceptionReport;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Stateless
public class ReservationSessionBean implements ReservationSessionBeanRemote, ReservationSessionBeanLocal {

    @PersistenceContext(unitName = "HotelReservationSystem-ejbPU")
    private EntityManager em;

    @EJB
    private RoomAllocationSessionBeanLocal roomAllocationSessionBean;

    public Long createReservation(Reservation reservation) {
        em.persist(reservation);
        em.flush();
        return reservation.getReservationId();
    }

    public Reservation retrieveReservationById(Long reservationId) {
        return em.find(Reservation.class, reservationId);
    }

    public List<Reservation> retrieveAllReservations() {
        return em.createQuery("SELECT r FROM Reservation r", Reservation.class).getResultList();
    }

    public void updateReservation(Reservation reservation) {
        em.merge(reservation);
    }

    public void deleteReservation(Long reservationId) {
        Reservation reservation = retrieveReservationById(reservationId);
        if (reservation != null) {
            em.remove(reservation);
        }
    }

    // New method to create walk-in reservations and handle immediate allocation if necessary
    public void createWalkInReservations(List<Reservation> reservations, LocalDate checkInDate) {
        for (Reservation reservation : reservations) {
            em.persist(reservation);
            em.flush();
        }

        // Check if immediate allocation is needed for same-day check-in after 2 AM
        LocalTime currentTime = LocalTime.now();
        if (checkInDate.equals(LocalDate.now()) && currentTime.isAfter(LocalTime.of(2, 0))) {
            for (Reservation reservation : reservations) {
                roomAllocationSessionBean.allocateRoomForReservation(reservation);
            }
        }
    }
}
