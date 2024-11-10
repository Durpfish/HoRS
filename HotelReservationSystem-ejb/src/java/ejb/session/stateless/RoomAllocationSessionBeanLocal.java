package ejb.session.stateless;

import entity.Reservation;
import entity.RoomAllocationExceptionReport;
import java.time.LocalDate;
import java.util.List;
import javax.ejb.Local;
import javax.ejb.Timer;

@Local
public interface RoomAllocationSessionBeanLocal {

    public void allocateRoomsDaily(Timer timer);

    public void allocateRoomsForDate(LocalDate date);
    
    public List<RoomAllocationExceptionReport> viewAllRoomAllocationExceptions();

    public void allocateRoomForReservation(Reservation reservation);

    public void handleManualRoomAllocationException(Long reservationId, String message);

    public void allocateRoomsForOnlineReservation(Reservation reservation);
}
