package ejb.session.stateless;

import entity.RoomType;
import java.time.LocalDate;
import java.util.Collections;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.persistence.NoResultException;
import util.enumeration.roomStatus;


@Stateless
public class RoomTypeSessionBean implements RoomTypeSessionBeanRemote, RoomTypeSessionBeanLocal {

    @EJB
    private RoomSessionBeanLocal roomSessionBean;
    
    @PersistenceContext(unitName = "HotelReservationSystem-ejbPU")
    private EntityManager em;

    public Long createRoomType(RoomType roomType) {
        em.persist(roomType);   
        em.flush();
        return roomType.getRoomTypeId();
    }

    public RoomType retrieveRoomTypeById(Long roomTypeId) {
        return em.find(RoomType.class, roomTypeId);
    }

    public List<RoomType> retrieveAllRoomTypes() {
        return em.createQuery("SELECT rt FROM RoomType rt", RoomType.class).getResultList();
    }

   public List<RoomType> retrieveAvailableRoomTypes(LocalDate checkInDate, LocalDate checkOutDate) {
        List<RoomType> availableRoomTypes = em.createQuery(
            "SELECT rt FROM RoomType rt WHERE rt.disabled = false " +
            "AND EXISTS (SELECT r FROM Room r WHERE r.roomType = rt AND r.status = :status " +
            "AND r.roomId NOT IN (" +
            "    SELECT ra.room.roomId FROM Reservation res " +
            "    JOIN res.roomAllocation ra " +
            "    WHERE res.checkOutDate > :checkInDate AND res.checkInDate < :checkOutDate" +
            "))", RoomType.class)
            .setParameter("status", roomStatus.AVAILABLE)
            .setParameter("checkInDate", checkInDate)
            .setParameter("checkOutDate", checkOutDate)
            .getResultList();

        // Ensure an empty list is returned if no results are found, instead of null
        return availableRoomTypes != null ? availableRoomTypes : Collections.emptyList();
    }


    public void updateRoomType(RoomType roomType) {
        em.merge(roomType);
    }

    public void deleteRoomType(Long roomTypeId) {
        RoomType roomType = retrieveRoomTypeById(roomTypeId);
        if (roomType != null) {
            long roomCount = em.createQuery("SELECT COUNT(r) FROM Room r WHERE r.roomType.roomTypeId = :roomTypeId", Long.class)
                               .setParameter("roomTypeId", roomTypeId)
                               .getSingleResult();
            long reservationCount = em.createQuery("SELECT COUNT(r) FROM Reservation r WHERE r.roomType.roomTypeId = :roomTypeId", Long.class)
                                      .setParameter("roomTypeId", roomTypeId)
                                      .getSingleResult();
            if (roomCount > 0 || reservationCount > 0) {
                throw new IllegalArgumentException("Cannot delete room type as it is associated with existing rooms or reservations.");
            }
            em.remove(roomType);
        }
    }
    
    public RoomType retrieveRoomTypeByName(String name) {
        try {
            return em.createQuery("SELECT rt FROM RoomType rt WHERE rt.name = :name", RoomType.class)
                     .setParameter("name", name)
                     .getSingleResult();
        } catch (NoResultException e) {
            return null; // Return null if no room type with the specified name is found
        }
    }

}
