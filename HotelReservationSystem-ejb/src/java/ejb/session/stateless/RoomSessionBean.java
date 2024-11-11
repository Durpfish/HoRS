package ejb.session.stateless;

import entity.Room;
import entity.RoomType;
import java.time.LocalDate;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import javax.persistence.NoResultException;
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
        return em.createQuery("SELECT r FROM Room r WHERE r.status = :status", Room.class)
             .setParameter("status", roomStatus.AVAILABLE) 
             .getResultList();
    }
    
    public List<Room> retrieveAvailableRoomsForDates(LocalDate checkInDate, LocalDate checkOutDate) {
        return em.createQuery(
                "SELECT r FROM Room r " +
                "JOIN r.roomType rt " +
                "WHERE r.status = :status " +
                "AND rt.disabled = false " + 
                "AND r.roomId NOT IN (SELECT ra.room.roomId FROM Reservation res " +
                "JOIN res.roomAllocation ra " +
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
    
    public boolean hasAvailableRooms(RoomType roomType, LocalDate checkInDate, LocalDate checkOutDate) {
        List<Room> availableRooms = em.createQuery(
                "SELECT r FROM Room r " +
                "WHERE r.roomType = :roomType " +
                "AND r.status = :status " +
                "AND r.roomId NOT IN (" +
                "    SELECT ra.room.roomId FROM Reservation res " +
                "    JOIN res.roomAllocation ra " +
                "    WHERE res.checkOutDate > :checkInDate AND res.checkInDate < :checkOutDate" +
                ")", Room.class)
                .setParameter("roomType", roomType)
                .setParameter("status", roomStatus.AVAILABLE)
                .setParameter("checkInDate", checkInDate)
                .setParameter("checkOutDate", checkOutDate)
                .getResultList();

        return !availableRooms.isEmpty();
    }   
    
    public List<Room> retrieveAvailableRoomsForRoomType(RoomType roomType, LocalDate checkInDate, LocalDate checkOutDate) {
        return em.createQuery(
                "SELECT r FROM Room r " +
                "WHERE r.roomType = :roomType " +
                "AND r.status = :status " +
                "AND r.roomId NOT IN (" +
                "    SELECT ra.room.roomId FROM Reservation res " +
                "    JOIN res.roomAllocation ra " +
                "    WHERE res.checkOutDate > :checkInDate AND res.checkInDate < :checkOutDate" +
                ")", Room.class)
                .setParameter("roomType", roomType)
                .setParameter("status", roomStatus.AVAILABLE)
                .setParameter("checkInDate", checkInDate)
                .setParameter("checkOutDate", checkOutDate)
                .getResultList();
    }
    
    public Room retrieveRoomByRoomNumber(String roomNumber) {
        try {
            return em.createQuery("SELECT r FROM Room r WHERE r.roomNumber = :roomNumber", Room.class)
                     .setParameter("roomNumber", roomNumber)
                     .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
}
