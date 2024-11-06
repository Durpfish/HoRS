package ejb.session.stateless;

import entity.Reservation;
import entity.Room;
import entity.RoomAllocationExceptionReport;
import entity.RoomType;
import javax.ejb.Schedule;
import javax.ejb.Stateless;
import javax.ejb.Timer;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDate;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import util.enumeration.roomStatus;

@Stateless
public class RoomAllocationSessionBean implements RoomAllocationSessionBeanRemote, RoomAllocationSessionBeanLocal {

    @PersistenceContext(unitName = "HotelReservationSystem-ejbPU")
    private EntityManager em;

    private static final Logger LOGGER = Logger.getLogger(RoomAllocationSessionBean.class.getName());

    // Scheduled task to run daily at 2 am
    @Schedule(hour = "2", minute = "0", persistent = false)
    @Override
    public void allocateRoomsDaily(Timer timer) {
        LocalDate today = LocalDate.now();
        allocateRoomsForDate(today);
    }

    // Allocates rooms for reservations on the specified date
    @Override
    public void allocateRoomsForDate(LocalDate date) {
        // Retrieve reservations for the specified date, ordered by room type for priority handling.
        List<Reservation> reservations = em.createQuery(
            "SELECT r FROM Reservation r WHERE r.checkInDate = :date ORDER BY r.roomType.roomOrder ASC", Reservation.class)
            .setParameter("date", date)
            .getResultList();

        for (Reservation reservation : reservations) {
            Room allocatedRoom = findAvailableRoomOrUpgrade(reservation.getRoomType());

            if (allocatedRoom != null) {
                reservation.setAllocatedRoom(allocatedRoom);
                allocatedRoom.setStatus(roomStatus.UNAVAILABLE); // Mark room as unavailable
                em.merge(reservation);
                em.merge(allocatedRoom);

                // Log an upgrade allocation if the room type does not match the requested type.
                if (!allocatedRoom.getRoomType().equals(reservation.getRoomType())) {
                    logRoomAllocationException(reservation, "Type 1: Room upgrade allocated to next higher tier.");
                }

            } else {
                // Log a Type 2 exception if no suitable room or upgrade is available.
                logRoomAllocationException(reservation, "Type 2: No rooms available, manual handling required.");
            }
        }
    }
    
    @Override
    public void allocateRoomForReservation(Reservation reservation) {
        Room allocatedRoom = findAvailableRoomOrUpgrade(reservation.getRoomType());

        if (allocatedRoom != null) {
            reservation.setAllocatedRoom(allocatedRoom);
            allocatedRoom.setStatus(roomStatus.UNAVAILABLE); // Mark room as unavailable
            em.merge(reservation);
            em.merge(allocatedRoom);

            if (!allocatedRoom.getRoomType().equals(reservation.getRoomType())) {
                // Log an upgrade allocation as a "Type 1" exception
                logRoomAllocationException(reservation, "Type 1: Room upgrade allocated to next higher tier.");
            }
        } else {
            // Log a "Type 2" exception if no suitable room is available
            logRoomAllocationException(reservation, "Type 2: No rooms available, manual handling required.");
        }
    }
    
    // Tries to find an available room in the requested room type or the next higher tier
    private Room findAvailableRoomOrUpgrade(RoomType requestedRoomType) {
        RoomType currentRoomType = requestedRoomType;

        while (currentRoomType != null) {
            Room availableRoom = findAvailableRoom(currentRoomType);
            if (availableRoom != null) {
                return availableRoom;
            }
            currentRoomType = getNextHigherRoomType(currentRoomType);
        }
        
        return null; // No rooms available in the requested or higher tiers
    }

    // Finds an available room in the specified room type
    private Room findAvailableRoom(RoomType roomType) {
        List<Room> availableRooms = em.createQuery(
            "SELECT r FROM Room r WHERE r.roomType = :roomType AND r.status = :status", Room.class)
            .setParameter("roomType", roomType)
            .setParameter("status", roomStatus.AVAILABLE)
            .getResultList();

        return availableRooms.isEmpty() ? null : availableRooms.get(0);
    }

    // Retrieves the next higher room type based on the `order` field
    private RoomType getNextHigherRoomType(RoomType currentRoomType) {
        List<RoomType> higherRoomTypes = em.createQuery(
            "SELECT rt FROM RoomType rt WHERE rt.roomOrder > :currentOrder ORDER BY rt.roomOrder ASC", RoomType.class)
            .setParameter("currentOrder", currentRoomType.getRoomOrder())
            .setMaxResults(1)
            .getResultList();

        return higherRoomTypes.isEmpty() ? null : higherRoomTypes.get(0);
    }

    // Logs a room allocation exception by creating a RoomAllocationExceptionReport
    private void logRoomAllocationException(Reservation reservation, String message) {
        RoomAllocationExceptionReport exceptionReport = new RoomAllocationExceptionReport();
        exceptionReport.setReservation(reservation);
        exceptionReport.setMessage(message);
        exceptionReport.setExceptionDate(LocalDate.now()); // Set the current date as exception date
        em.persist(exceptionReport);

        LOGGER.log(Level.WARNING, "Room allocation exception for reservation ID: {0} - {1}", new Object[]{reservation.getReservationId(), message});
    }
    
    @Override
    public void handleManualRoomAllocationException(Long reservationId, String message) {
        Reservation reservation = em.find(Reservation.class, reservationId);
        if (reservation != null) {
            logRoomAllocationException(reservation, message);
        } else {
            LOGGER.log(Level.WARNING, "No reservation found with ID: {0}. Cannot log exception.", reservationId);
        }
    }


    // Retrieves all room allocation exceptions
    @Override
    public List<RoomAllocationExceptionReport> viewAllRoomAllocationExceptions() {
        return em.createQuery("SELECT e FROM RoomAllocationExceptionReport e", RoomAllocationExceptionReport.class)
                 .getResultList();
    }
}

//    public void allocateRoomsForDate(LocalDate date) {
//        List<Reservation> reservations = em.createQuery(
//            "SELECT r FROM Reservation r WHERE r.checkInDate = :date ORDER BY r.roomType.roomTypeId ASC", Reservation.class)
//            .setParameter("date", date)
//            .getResultList();
//
//        for (Reservation reservation : reservations) {
//            Room allocatedRoom = findAvailableRoomOrUpgrade(reservation.getRoomType());
//
//            if (allocatedRoom != null) {
//                reservation.setAllocatedRoom(allocatedRoom);
//                allocatedRoom.setStatus(roomStatus.UNAVAILABLE); // Mark room as unavailable
//                em.merge(reservation);
//                em.merge(allocatedRoom);
//
//                if (!allocatedRoom.getRoomType().equals(reservation.getRoomType())) {
//                    // Log a "Type 1" exception when an upgrade is allocated
//                    logRoomAllocationException(reservation, "Type 1: Room upgrade allocated to next higher tier.");
//                }
//
//            } else {
//                // Log a "Type 2" exception if no suitable room is available
//                logRoomAllocationException(reservation, "Type 2: No rooms available, manual handling required.");
//            }
//        }
//    }
    