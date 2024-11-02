/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/SessionRemote.java to edit this template
 */
package ejb.session.stateless;

import entity.Reservation;
import entity.RoomAllocationExceptionReport;
import java.time.LocalDate;
import java.util.List;
import javax.ejb.Remote;
import javax.ejb.Timer;

/**
 *
 * @author Calvin
 */
@Remote
public interface RoomAllocationSessionBeanRemote {
    public void allocateRoomsDaily(Timer timer);
    
    public void allocateRoomsForDate(LocalDate date);
    
    public List<RoomAllocationExceptionReport> viewAllRoomAllocationExceptions() ;
    
    public void allocateRoomForReservation(Reservation reservation);
    
    public void handleManualRoomAllocationException(Long reservationId, String message);
}
