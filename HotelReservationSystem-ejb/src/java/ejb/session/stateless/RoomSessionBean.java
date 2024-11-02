package ejb.session.stateless;

import entity.Room;
import entity.RoomType;
import java.time.LocalDate;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import util.enumeration.roomStatus;

@Stateless
public class RoomSessionBean implements RoomSessionBeanRemote, RoomSessionBeanLocal {

    @PersistenceContext(unitName = "HotelReservationSystem-ejbPU")
    private EntityManager em;

    public Long createRoom(Room room) {
        em.persist(room);
        em.flush();
        return room.getRoomId();
    }

    public Room retrieveRoomById(Long roomId) {
        return em.find(Room.class, roomId);
    }

    public List<Room> retrieveAllRooms() {
        return em.createQuery("SELECT r FROM Room r", Room.class).getResultList();
    }

    public List<Room> retrieveRoomsByRoomType(RoomType roomType) {
        return em.createQuery("SELECT r FROM Room r WHERE r.roomType = :roomType", Room.class)
                 .setParameter("roomType", roomType)
                 .getResultList();
    }

    public List<Room> retrieveAvailableRooms() {
        return em.createQuery("SELECT r FROM Room r WHERE r.status = 'AVAILABLE'", Room.class)
                 .getResultList();
    }
    
    public List<Room> retrieveAvailableRoomsForDates(LocalDate checkInDate, LocalDate checkOutDate) {
        return em.createQuery(
                "SELECT r FROM Room r WHERE r.status = :status " +
                "AND r.roomType IN (SELECT rt FROM RoomType rt WHERE rt.disabled = false) " + 
                "AND r.roomId NOT IN (SELECT res.room.roomId FROM Reservation res " +
                "WHERE res.checkOutDate > :checkInDate AND res.checkInDate < :checkOutDate)", Room.class)
                .setParameter("status", roomStatus.AVAILABLE)
                .setParameter("checkInDate", checkInDate)
                .setParameter("checkOutDate", checkOutDate)
                .getResultList();
    }

    public void updateRoom(Room room) {
        em.merge(room);
    }

    public void deleteRoom(Long roomId) {
        Room room = retrieveRoomById(roomId);
        if (room != null) {
            long reservationCount = em.createQuery("SELECT COUNT(ra) FROM RoomAllocation ra WHERE ra.room.roomId = :roomId", Long.class)
                          .setParameter("roomId", roomId)
                          .getSingleResult();
            if (reservationCount > 0) {
                throw new IllegalArgumentException("Cannot delete room as it is associated with existing reservations.");
            }
            em.remove(room);
        }
    }
}
