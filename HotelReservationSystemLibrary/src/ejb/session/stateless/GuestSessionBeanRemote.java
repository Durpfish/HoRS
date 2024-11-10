/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/SessionRemote.java to edit this template
 */
package ejb.session.stateless;

import entity.Guest;
import entity.Reservation;
import java.util.List;
import javax.ejb.Remote;

/**
 *
 * @author josalyn
 */
@Remote
public interface GuestSessionBeanRemote {
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
