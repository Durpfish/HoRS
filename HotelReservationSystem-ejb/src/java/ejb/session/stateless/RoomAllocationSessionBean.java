package ejb.session.stateless;

import entity.Reservation;
import entity.Room;
import entity.RoomAllocation;
import entity.RoomAllocationExceptionReport;
import entity.RoomType;
import util.enumeration.ExceptionType;
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

    @Schedule(hour = "2", minute = "0", persistent = false)
    @Override
    public void allocateRoomsDaily(Timer timer) {
        LocalDate today = LocalDate.now();
        allocateRoomsForDate(today);
    }

    @Override
    public void allocateRoomsForDate(LocalDate date) {
        List<Reservation> reservations = em.createQuery(
            "SELECT r FROM Reservation r WHERE r.checkInDate = :date ORDER BY r.roomType.name ASC", Reservation.class)
            .setParameter("date", date)
            .getResultList();

        for (Reservation reservation : reservations) {
            Room allocatedRoom = findAvailableRoomOrUpgrade(reservation.getRoomType());

            if (allocatedRoom != null) {
                createRoomAllocation(reservation.getReservationId(), allocatedRoom.getRoomId());
                allocatedRoom.setStatus(roomStatus.UNAVAILABLE);
                em.merge(allocatedRoom);

                if (!allocatedRoom.getRoomType().equals(reservation.getRoomType())) {
                    logRoomAllocationException(reservation, "Room upgrade allocated to next higher tier.", ExceptionType.UPGRADE_ALLOCATED);
                }
            } else {
                logRoomAllocationException(reservation, "No rooms available, manual handling required.", ExceptionType.NO_ROOM_AVAILABLE);
            }
        }
    }

    @Override
    public void allocateRoomForReservation(Reservation reservation) {
        Room allocatedRoom = findAvailableRoomOrUpgrade(reservation.getRoomType());

        if (allocatedRoom != null) {
            createRoomAllocation(reservation.getReservationId(), allocatedRoom.getRoomId());
            allocatedRoom.setStatus(roomStatus.UNAVAILABLE);
            em.merge(allocatedRoom);

            if (!allocatedRoom.getRoomType().equals(reservation.getRoomType())) {
                logRoomAllocationException(reservation, "Room upgrade allocated to next higher tier.", ExceptionType.UPGRADE_ALLOCATED);
            }
        } else {
            logRoomAllocationException(reservation, "No rooms available, manual handling required.", ExceptionType.NO_ROOM_AVAILABLE);
        }
    }

    public void allocateRoomsForOnlineReservation(Reservation reservation) {
        Room allocatedRoom = findAvailableRoomOrUpgrade(reservation.getRoomType());

        if (allocatedRoom != null) {
            RoomAllocation roomAllocation = new RoomAllocation();
            roomAllocation.setAllocationDate(LocalDate.now());
            roomAllocation.setRoom(allocatedRoom);
            roomAllocation.setReservation(reservation);

            // Persist RoomAllocation without marking room as unavailable immediately for online flow.
            em.persist(roomAllocation);

            if (!allocatedRoom.getRoomType().equals(reservation.getRoomType())) {
                logRoomAllocationException(reservation, "Room upgrade allocated to next higher tier.", ExceptionType.UPGRADE_ALLOCATED);
            }
        } else {
            logRoomAllocationException(reservation, "No rooms available, manual handling required.", ExceptionType.NO_ROOM_AVAILABLE);
        }
    }

    private void createRoomAllocation(Long reservationId, Long roomId) {
        Reservation managedReservation = em.find(Reservation.class, reservationId);
        Room managedRoom = em.find(Room.class, roomId);

        if (managedReservation != null && managedRoom != null) {
            RoomAllocation roomAllocation = new RoomAllocation();
            roomAllocation.setAllocationDate(LocalDate.now());
            roomAllocation.setRoom(managedRoom);
            roomAllocation.setReservation(managedReservation);
            em.persist(roomAllocation);
        } else {
            LOGGER.log(Level.WARNING, "Cannot create RoomAllocation as reservation or room is not found.");
        }
    }

    private Room findAvailableRoomOrUpgrade(RoomType requestedRoomType) {
        RoomType currentRoomType = requestedRoomType;

        while (currentRoomType != null) {
            Room availableRoom = findAvailableRoom(currentRoomType);
            if (availableRoom != null) {
                return availableRoom;
            }
            currentRoomType = currentRoomType.getNextHigherRoomType(); // Use nextHigherRoomType relationship
        }

        return null;
    }

    private Room findAvailableRoom(RoomType roomType) {
        List<Room> availableRooms = em.createQuery(
            "SELECT r FROM Room r WHERE r.roomType = :roomType AND r.status = :status", Room.class)
            .setParameter("roomType", roomType)
            .setParameter("status", roomStatus.AVAILABLE)
            .getResultList();

        return availableRooms.isEmpty() ? null : availableRooms.get(0);
    }

    private void logRoomAllocationException(Reservation reservation, String message, ExceptionType exceptionType) {
        RoomAllocationExceptionReport exceptionReport = new RoomAllocationExceptionReport();
        exceptionReport.setReservation(reservation);
        exceptionReport.setMessage(message);
        exceptionReport.setExceptionDate(LocalDate.now());
        exceptionReport.setExceptionType(exceptionType);
        em.persist(exceptionReport);

        LOGGER.log(Level.WARNING, "Room allocation exception for reservation ID: {0} - {1} - {2}", 
                  new Object[]{reservation.getReservationId(), exceptionType, message});
    }

    @Override
    public void handleManualRoomAllocationException(Long reservationId, String message) {
        Reservation reservation = em.find(Reservation.class, reservationId);
        if (reservation != null) {
            logRoomAllocationException(reservation, message, ExceptionType.MANUAL_HANDLING);
        } else {
            LOGGER.log(Level.WARNING, "No reservation found with ID: {0}. Cannot log exception.", reservationId);
        }
    }

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
    