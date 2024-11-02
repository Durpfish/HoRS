package ejb.session.stateless;

import entity.Reservation;
import java.util.List;
import javax.ejb.Local;

@Local
public interface ReservationSessionBeanLocal {

    Long createReservation(Reservation reservation);

    Reservation retrieveReservationById(Long reservationId);

    List<Reservation> retrieveAllReservations();

    void updateReservation(Reservation reservation);

    void deleteReservation(Long reservationId);
}
