package ejb.session.stateless;

import entity.RoomType;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import javax.ejb.EJBException;

@Stateless
public class RoomTypeSessionBean implements RoomTypeSessionBeanRemote, RoomTypeSessionBeanLocal {

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

    public List<RoomType> retrieveAvailableRoomTypes() {
        return em.createQuery("SELECT rt FROM RoomType rt WHERE rt.disabled = false", RoomType.class)
                 .getResultList();
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
}
