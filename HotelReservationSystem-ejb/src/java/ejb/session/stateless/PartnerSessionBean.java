/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/StatelessEjbClass.java to edit this template
 */
package ejb.session.stateless;

import entity.Partner;
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
public class PartnerSessionBean implements PartnerSessionBeanRemote, PartnerSessionBeanLocal {

    @PersistenceContext(unitName = "HotelReservationSystem-ejbPU")
    private EntityManager em;
    
    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
    
    public void registerPartner(Partner partner) {
        em.persist(partner); // Save the partner to the database
    }

    public Partner loginPartner(String username, String password) {
        try {
            return em.createQuery("SELECT p FROM Partner p WHERE p.username = :username AND p.password = :password", Partner.class)
                     .setParameter("username", username)
                     .setParameter("password", password)
                     .getSingleResult();
        } catch (Exception e) {
            return null; // Handle exceptions as needed
        }
    }

    public List<Reservation> viewPartnerReservations(Long partnerId) {
        return em.createQuery("SELECT r FROM Reservation r WHERE r.partner.id = :partnerId", Reservation.class)
                 .setParameter("partnerId", partnerId)
                 .getResultList();
    }

    
}
