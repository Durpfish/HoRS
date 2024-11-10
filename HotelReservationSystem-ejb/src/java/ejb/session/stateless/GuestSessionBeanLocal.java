/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/SessionLocal.java to edit this template
 */
package ejb.session.stateless;

import entity.Guest;
import entity.Reservation;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author josalyn
 */
@Local
public interface GuestSessionBeanLocal {

    void registerGuest(Guest guest);

    Guest loginGuest(String username, String password);

    List<Reservation> viewReservations(Long guestId);

    void makeReservation(Long guestId, Reservation reservationDetails);

    Guest findGuestByUsername(String username);

    void updateGuestDetails(Guest guest);

    void deleteGuest(Long guestId);

    public Guest findGuestById(Long guestId);

    public Reservation viewReservationDetails(Long reservationId);
    
}
