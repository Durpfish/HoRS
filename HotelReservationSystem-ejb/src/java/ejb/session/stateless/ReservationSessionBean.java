package ejb.session.stateless;

import entity.Reservation;
import entity.Rate;
import entity.RoomType;
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
    
    @EJB
    private RateSessionBeanLocal rateSessionBean;

    // Existing createReservation method
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

    // Walk-in reservations with immediate allocation if necessary
    public void createWalkInReservations(List<Reservation> reservations, LocalDate checkInDate) {
        for (Reservation reservation : reservations) {
            em.persist(reservation);
            em.flush();
        }

        // Check if immediate allocation is needed for same-day check-in after 2 AM
        if (isImmediateAllocationNeeded(checkInDate)) {
            for (Reservation reservation : reservations) {
                roomAllocationSessionBean.allocateRoomForReservation(reservation);
            }
        }
    }
    
    // New method for creating online reservations with rate hierarchy consideration
    @Override
    public Long createOnlineReservation(Reservation reservationDetails, LocalDate checkInDate, LocalDate checkOutDate) {
        RoomType roomType = reservationDetails.getRoomType();

        // Get the applicable rate (Promotion > Peak > Normal)
        Rate rate = rateSessionBean.retrieveApplicableRate(roomType, checkInDate);
        if (rate == null) {
            throw new IllegalArgumentException("No applicable rate found for the selected room type.");
        }

        // Calculate the total amount based on the rate
        long nights = checkOutDate.toEpochDay() - checkInDate.toEpochDay();
        reservationDetails.setTotalAmount(rate.getRatePerNight() * nights);

        // Persist the reservation
        em.persist(reservationDetails);
        em.flush();

        // Room allocation is deferred until batch job runs
        return reservationDetails.getReservationId();
    }
    
    // Helper method to determine if immediate allocation is required for same-day check-in after 2 AM
    private boolean isImmediateAllocationNeeded(LocalDate checkInDate) {
        LocalDate today = LocalDate.now();
        LocalTime currentTime = LocalTime.now();
        return checkInDate.equals(today) && currentTime.isAfter(LocalTime.of(2, 0));
    }
}
