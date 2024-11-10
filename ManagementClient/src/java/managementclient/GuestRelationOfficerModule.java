/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package managementclient;

import ejb.session.stateless.RateSessionBeanRemote;
import ejb.session.stateless.ReservationSessionBeanRemote;
import ejb.session.stateless.RoomAllocationSessionBeanRemote;
import ejb.session.stateless.RoomSessionBeanRemote;
import entity.Rate;
import entity.Reservation;
import entity.Room;
import entity.RoomAllocation;
import entity.RoomType;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import util.enumeration.reservationType;
import util.enumeration.roomStatus;

/**
 *
 * @author josalyn
 */
public class GuestRelationOfficerModule {
    
    private RoomSessionBeanRemote roomSessionBean;
    private RateSessionBeanRemote rateSessionBean;
    private ReservationSessionBeanRemote reservationSessionBean;
    private RoomAllocationSessionBeanRemote roomAllocationSessionBean;
    private Scanner scanner = new Scanner(System.in);
    
    public GuestRelationOfficerModule(RoomSessionBeanRemote roomSessionBean, RateSessionBeanRemote rateSessionBean, 
                                      ReservationSessionBeanRemote reservationSessionBean, RoomAllocationSessionBeanRemote roomAllocationSessionBean) {
        this.roomSessionBean = roomSessionBean;
        this.rateSessionBean = rateSessionBean;
        this.reservationSessionBean = reservationSessionBean;
        this.roomAllocationSessionBean = roomAllocationSessionBean;
    }
    
    public void showMenu() {
        int choice;
        do {
            System.out.println("\n*** Front Office Module ***");
            System.out.println("1: Walk-in Search Room");
            System.out.println("2: Walk-in Reserve Room");
            System.out.println("3: Check-in Guest");
            System.out.println("4: Check-out Guest");
            System.out.println("5: Logout");

            System.out.print("Enter choice: ");
            choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    doWalkInSearchRoom();
                    break;
                case 2:
                    doWalkInReserveRoom();
                    break;
                case 3:
                    doCheckInGuest();
                    break;
                case 4:
                    doCheckOutGuest();
                    break;
                case 5:
                    System.out.println("Logging out...");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        } while (choice != 5);
    }
    
    private void doWalkInSearchRoom() {
        System.out.println("*** Walk-in Room Search ***");

        System.out.print("Enter check-in date (YYYY-MM-DD)> ");
        LocalDate checkInDate = LocalDate.parse(scanner.nextLine());

        System.out.print("Enter check-out date (YYYY-MM-DD)> ");
        LocalDate checkOutDate = LocalDate.parse(scanner.nextLine());

        // Retrieve available rooms based on room allocation and availability dates
        List<Room> availableRooms = roomSessionBean.retrieveAvailableRoomsForDates(checkInDate, checkOutDate);

        System.out.println("\nAvailable Rooms:");
        for (Room room : availableRooms) {
            RoomType roomType = room.getRoomType();

            // Fetch the published rate for this room type
            Rate publishedRate = rateSessionBean.retrievePublishedRateForRoomType(roomType.getRoomTypeId());

            if (publishedRate != null) {
                double totalCost = publishedRate.getRatePerNight() * (checkOutDate.toEpochDay() - checkInDate.toEpochDay());
                System.out.printf("Room ID: %d, Room Type: %s, Rate Per Night: %.2f, Total Cost: %.2f\n",
                              room.getRoomId(), roomType.getName(), publishedRate.getRatePerNight(), totalCost);
            } else {
                System.out.printf("Room ID: %d, Room Type: %s, Rate information not available.\n",
                              room.getRoomId(), roomType.getName());
            }
        }
    }
    
    
    private void doWalkInReserveRoom() {
        System.out.println("*** Walk-in Room Reservation ***");
    
        // Step 1: Get and validate dates
        System.out.print("Enter check-in date (YYYY-MM-DD)> ");
        LocalDate checkInDate = LocalDate.parse(scanner.nextLine());
    
        System.out.print("Enter check-out date (YYYY-MM-DD)> ");
        LocalDate checkOutDate = LocalDate.parse(scanner.nextLine());
            
        // Validate dates
        LocalDate today = LocalDate.now();
        if (checkInDate.isBefore(today)) {
            System.out.println("Check-in date cannot be in the past.");
            return;
        }
    
        if (checkOutDate.isBefore(checkInDate)) {
            System.out.println("Check-out date must be after check-in date.");
            return;
        }
        
        // Step 2: Search and display available rooms with rates
        List<Room> availableRooms = roomSessionBean.retrieveAvailableRoomsForDates(checkInDate, checkOutDate);
        if (availableRooms.isEmpty()) {
            System.out.println("No rooms available for the selected dates.");
            return;
        }

        // Group rooms by room type for better display
        Map<RoomType, List<Room>> roomsByType = new HashMap<>();
        Map<RoomType, Rate> ratesByType = new HashMap<>();
    
        for (Room room : availableRooms) {
            RoomType type = room.getRoomType();
            roomsByType.computeIfAbsent(type, k -> new ArrayList<>()).add(room);
            if (!ratesByType.containsKey(type)) {
                Rate rate = rateSessionBean.retrievePublishedRateForRoomType(type.getRoomTypeId());
                ratesByType.put(type, rate);
            }
        }

        // Display available room types
        System.out.println("\nAvailable Room Types:");
        int counter = 1;
        Map<Integer, RoomType> roomTypeSelectionMap = new HashMap<>();
    
        for (Map.Entry<RoomType, List<Room>> entry : roomsByType.entrySet()) {
            RoomType type = entry.getKey();
            Rate rate = ratesByType.get(type);
            double ratePerNight = rate != null ? rate.getRatePerNight() : 0.0;
        
            System.out.printf("%d. %s - Rate per night: $%.2f (Available rooms: %d)\n", 
                counter, 
                type.getName(), 
                ratePerNight,
                entry.getValue().size());
            roomTypeSelectionMap.put(counter++, type);
        }

        // Step 3: Get number of rooms to reserve
        int numberOfRooms;
        while (true) {
            System.out.print("\nEnter number of rooms to reserve> ");
            numberOfRooms = scanner.nextInt();
            scanner.nextLine(); // Consume newline
        
            if (numberOfRooms <= 0) {
                System.out.println("Please enter a valid number of rooms (greater than 0).");
            } else if (numberOfRooms > availableRooms.size()) {
                System.out.println("Not enough rooms available. Maximum available: " + availableRooms.size());
            } else {
                break;
            }
        }

        // Step 4: Room type selection and reservation creation
        List<Reservation> reservations = new ArrayList<>();
        double totalAmount = 0.0;

        for (int i = 0; i < numberOfRooms; i++) {
            // Select room type
            int typeSelection;
            RoomType selectedType;
            while (true) {
                System.out.printf("Select room type for room #%d (1-%d)> ", i + 1, roomTypeSelectionMap.size());
                typeSelection = scanner.nextInt();
                scanner.nextLine(); // Consume newline

                if (!roomTypeSelectionMap.containsKey(typeSelection)) {
                    System.out.println("Invalid selection. Please choose a valid room type.");
                    continue;
                }
            
                selectedType = roomTypeSelectionMap.get(typeSelection);
                int availableOfType = roomsByType.get(selectedType).size();
            
                if (availableOfType <= 0) {
                    System.out.println("No more rooms available of type " + selectedType.getName());
                    continue;
                }
            
                // Update available count for this room type
                roomsByType.get(selectedType).remove(0);
                break;
            }
        
            Rate rate = ratesByType.get(selectedType);
            if (rate == null) {
                System.out.println("Error: No published rate available for room type: " + selectedType.getName());
                return;
            }

            // Calculate reservation amount
            long numberOfNights = checkOutDate.toEpochDay() - checkInDate.toEpochDay();
            double reservationAmount = rate.getRatePerNight() * numberOfNights;
            totalAmount += reservationAmount;

            // Create reservation
            Reservation reservation = new Reservation();
            reservation.setCheckInDate(checkInDate);
            reservation.setCheckOutDate(checkOutDate);
            reservation.setNumberOfGuests(1); // Default for walk-in
            reservation.setRoomType(selectedType);
            reservation.setReservationType(reservationType.WALK_IN);
        
            reservations.add(reservation);
        }

        // Step 5: Process reservations and handle immediate allocation if needed
        List<Long> reservationIds = new ArrayList<>();
        for (Reservation reservation : reservations) {
            Long reservationId = reservationSessionBean.createReservation(reservation);
            reservationIds.add(reservationId);
        }

        // Handle same-day check-in after 2 AM
        LocalTime currentTime = LocalTime.now();
        if (checkInDate.equals(LocalDate.now()) && currentTime.isAfter(LocalTime.of(2, 0))) {
            System.out.println("\nProcessing immediate room allocation for same-day check-in after 2 AM...");
            for (Long reservationId : reservationIds) {
                Reservation reservation = reservationSessionBean.retrieveReservationById(reservationId);
                if (reservation != null) {
                    roomAllocationSessionBean.allocateRoomForReservation(reservation);
                }
            }
            System.out.println("Room allocation completed.");
        }

        // Display summary
        System.out.println("\nReservation Summary:");
        System.out.printf("Number of rooms reserved: %d\n", numberOfRooms);
        System.out.printf("Total amount: $%.2f\n", totalAmount);
        System.out.println("Reservation IDs: " + reservationIds);
    }



    private void doCheckInGuest() {
        System.out.println("*** Guest Check-In ***");

        System.out.print("Enter Reservation ID> ");
        Long reservationId = scanner.nextLong();
        scanner.nextLine(); // Consume newline

        Reservation reservation = reservationSessionBean.retrieveReservationById(reservationId);

        if (reservation == null) {
            System.out.println("Reservation not found.");
            return;
        }

        // Attempt to retrieve allocated room from the reservation
        RoomAllocation roomAllocation = reservation.getRoomAllocation();
        if (roomAllocation != null && roomAllocation.getRoom() != null) {
            Room allocatedRoom = roomAllocation.getRoom();

            // Mark the allocated room as occupied
            allocatedRoom.setStatus(roomStatus.UNAVAILABLE);
            roomSessionBean.updateRoom(allocatedRoom);

            System.out.println("Guest checked into Room " + allocatedRoom.getRoomNumber() + ".");
            System.out.println("Check-in successful!");
        } else {
            // Handle cases where no room has been allocated
            System.out.println("No room has been allocated for this reservation.");
            System.out.println("This requires manual handling by front-desk staff.");

            // Log the exception for manual handling in the system
            try {
                roomAllocationSessionBean.handleManualRoomAllocationException(reservationId, "Manual handling required: No room allocated.");
                System.out.println("Room allocation exception has been logged successfully for manual handling.");
            } catch (Exception ex) {
                System.out.println("An error occurred while logging the exception: " + ex.getMessage());
            }
        }
    }


   private void doCheckOutGuest() {
        System.out.println("*** Guest Check-Out ***");

        System.out.print("Enter Reservation ID> ");
        Long reservationId = scanner.nextLong();
        scanner.nextLine(); // Consume newline

        Reservation reservation = reservationSessionBean.retrieveReservationById(reservationId);

        if (reservation == null) {
            System.out.println("Reservation not found.");
            return;
        }

        // Attempt to retrieve the allocated room from the RoomAllocation entity
        RoomAllocation roomAllocation = reservation.getRoomAllocation();
        if (roomAllocation != null && roomAllocation.getRoom() != null) {
            Room allocatedRoom = roomAllocation.getRoom();

            // Mark the room as available after check-out
            allocatedRoom.setStatus(roomStatus.AVAILABLE);
            roomSessionBean.updateRoom(allocatedRoom);

            System.out.println("Guest checked out from Room " + allocatedRoom.getRoomNumber() + ".");
            System.out.println("Check-out successful!");
        } else {
            // If no room was allocated, inform the user
            System.out.println("No room was allocated to this reservation. Check-out cannot be processed.");
        }
    }
}
