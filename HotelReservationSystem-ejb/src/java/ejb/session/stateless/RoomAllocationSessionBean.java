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
import javax.persistence.Query;
import util.enumeration.roomStatus;

@Stateless
public class RoomAllocationSessionBean implements RoomAllocationSessionBeanRemote, RoomAllocationSessionBeanLocal {

    @PersistenceContext(unitName = "HotelReservationSystem-ejbPU")
    private EntityManager em;

    private static final Logger LOGGER = Logger.getLogger(RoomAllocationSessionBean.class.getName());

    @Schedule(hour = "2", minute = "00", timezone = "Asia/Singapore", persistent = true)
    @Override
    public void allocateRoomsDaily(Timer timer) {
        try {
            LocalDate today = LocalDate.now();
            System.out.println("Starting daily allocation for date: " + today);
        
            // Get reservations for today
            List<Reservation> reservations = em.createQuery(
                "SELECT r FROM Reservation r WHERE r.checkInDate = :date ORDER BY r.roomType.name ASC", 
                Reservation.class)
                .setParameter("date", today)
                .getResultList();
            
            System.out.println("Found " + reservations.size() + " reservations for today");

            for (Reservation reservation : reservations) {
                System.out.println("Processing reservation ID: " + reservation.getReservationId());
                allocateRoomForReservation(reservation);
            }
        } catch (Exception e) {
            System.err.println("Error in daily allocation: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void allocateRoomsForDate(LocalDate date) {
        List<Reservation> reservations = em.createQuery(
            "SELECT r FROM Reservation r WHERE r.checkInDate = :date ORDER BY r.roomType.name ASC", Reservation.class)
            .setParameter("date", date)
            .getResultList();

        for (Reservation reservation : reservations) {
            List<RoomAllocation> existingAllocations = em.createQuery(
                "SELECT ra FROM RoomAllocation ra WHERE ra.reservation.reservationId = :reservationId", RoomAllocation.class)
                .setParameter("reservationId", reservation.getReservationId())
                .getResultList();

            if (!existingAllocations.isEmpty()) {
                // If room is already allocated for this reservation, skip allocation
                continue;
            }
            
            Room allocatedRoom = findAvailableRoomOrUpgrade(reservation.getRoomType());

            if (allocatedRoom != null) {
                createRoomAllocation(reservation.getReservationId(), allocatedRoom.getRoomId());
                allocatedRoom.setStatus(roomStatus.UNAVAILABLE);
                em.merge(allocatedRoom);

                if (!allocatedRoom.getRoomType().equals(reservation.getRoomType())) {
                    logRoomAllocationException(reservation, String.format("Room upgraded from %s to %s", reservation.getRoomType().getName(),
                            allocatedRoom.getRoomType().getName()), ExceptionType.UPGRADE_ALLOCATED);
                }
            } else {
                logRoomAllocationException(reservation, "No rooms available, manual handling required.", ExceptionType.NO_ROOM_AVAILABLE);
            }
        }
    }

    public void allocateRoomForReservation(Reservation reservation) {
        try {
            System.out.println("Starting allocation for reservation: " + reservation.getReservationId());
        
            // Check existing allocations
            List<RoomAllocation> existingAllocations = em.createQuery(
                "SELECT ra FROM RoomAllocation ra WHERE ra.reservation.reservationId = :reservationId", 
                RoomAllocation.class)
                .setParameter("reservationId", reservation.getReservationId())
                .getResultList();

            if (!existingAllocations.isEmpty()) {
                System.out.println("Reservation " + reservation.getReservationId() + " already has allocation");
                return;
            }

            // Find available room
            Room allocatedRoom = findAvailableRoomOrUpgrade(reservation.getRoomType());
            System.out.println("Found room: " + (allocatedRoom != null ? allocatedRoom.getRoomId() : "null"));

            if (allocatedRoom != null) {
                createRoomAllocation(reservation.getReservationId(), allocatedRoom.getRoomId());
                allocatedRoom.setStatus(roomStatus.UNAVAILABLE);
                em.merge(allocatedRoom);
                em.flush();
                System.out.println("Successfully allocated room " + allocatedRoom.getRoomId() + 
                             " for reservation " + reservation.getReservationId());
            } else {
                System.out.println("No available rooms found for reservation " + reservation.getReservationId());
            }
        } catch (Exception e) {
            System.err.println("Error allocating room: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void allocateRoomsForOnlineReservation(Reservation reservation) {
        List<RoomAllocation> existingAllocations = em.createQuery(
            "SELECT ra FROM RoomAllocation ra WHERE ra.reservation.reservationId = :reservationId", RoomAllocation.class)
            .setParameter("reservationId", reservation.getReservationId())
            .getResultList();

        if (!existingAllocations.isEmpty()) {
            // Room already allocated for this reservation, skip further allocation
            return;
        }
        
        Room allocatedRoom = findAvailableRoomOrUpgrade(reservation.getRoomType());

        if (allocatedRoom != null) {
            RoomAllocation roomAllocation = new RoomAllocation();
            roomAllocation.setAllocationDate(LocalDate.now());
            roomAllocation.setRoom(allocatedRoom);
            roomAllocation.setReservation(reservation);
            em.persist(roomAllocation);

            if (!allocatedRoom.getRoomType().equals(reservation.getRoomType())) {
                logRoomAllocationException(reservation, String.format("Room upgraded from %s to %s", reservation.getRoomType().getName(),
                            allocatedRoom.getRoomType().getName()), ExceptionType.UPGRADE_ALLOCATED);
            }
        } else {
            logRoomAllocationException(reservation, "No rooms available, manual handling required.", ExceptionType.NO_ROOM_AVAILABLE);
        }
    }

    private void createRoomAllocation(Long reservationId, Long roomId) {
        try {
            System.out.println("Creating allocation for reservation " + reservationId + " with room " + roomId);
            Reservation managedReservation = em.find(Reservation.class, reservationId);
            Room managedRoom = em.find(Room.class, roomId);

            if (managedReservation != null && managedRoom != null) {
                RoomAllocation roomAllocation = new RoomAllocation();
                roomAllocation.setAllocationDate(LocalDate.now());
                roomAllocation.setRoom(managedRoom);
                roomAllocation.setReservation(managedReservation);
                em.persist(roomAllocation);
                em.flush();
                System.out.println("Successfully created room allocation");
            } else {
                System.out.println("Failed to create allocation - reservation or room not found");
            }
        } catch (Exception e) {
            System.err.println("Error creating room allocation: " + e.getMessage());
            e.printStackTrace();
        }
    }


    private Room findAvailableRoomOrUpgrade(RoomType requestedRoomType) {
        try {
            System.out.println("Searching for room of type: " + requestedRoomType.getName());
            RoomType currentRoomType = requestedRoomType;

            while (currentRoomType != null) {
                Room availableRoom = findAvailableRoom(currentRoomType);
                if (availableRoom != null) {
                    System.out.println("Found available room: " + availableRoom.getRoomId());
                    return availableRoom;
                }
                currentRoomType = currentRoomType.getNextHigherRoomType();
                System.out.println("Trying next room type: " + (currentRoomType != null ? currentRoomType.getName() : "none available"));
            }
        } catch (Exception e) {
            System.err.println("Error finding available room: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    private Room findAvailableRoom(RoomType roomType) {
        try {
            List<Room> availableRooms = em.createQuery(
                "SELECT r FROM Room r WHERE r.roomType = :roomType AND r.status = :status " +
                "ORDER BY r.roomNumber ASC", Room.class)
                .setParameter("roomType", roomType)
                .setParameter("status", roomStatus.AVAILABLE)
                .getResultList();
        
            System.out.println("Found " + availableRooms.size() + " available rooms for type " + 
                              roomType.getName());
            return availableRooms.isEmpty() ? null : availableRooms.get(0);
        } catch (Exception e) {
            System.err.println("Error finding available room: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
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
    