package ejb.session.stateless;

import entity.Reservation;
import java.time.LocalDate;
import java.util.List;
import javax.ejb.Remote;

@Remote
public interface ReservationSessionBeanRemote {

    Long createReservation(Reservation reservation);

    Reservation retrieveReservationById(Long reservationId);

    List<Reservation> retrieveAllReservations();

    void updateReservation(Reservation reservation);

    void deleteReservation(Long reservationId);
    
    public void createWalkInReservations(List<Reservation> reservations, LocalDate checkInDate);
}
