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
import entity.RoomType;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
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
        System.out.println("*** Walk-in Reserve Room ***");

        // Step 1: Get check-in and check-out dates
        System.out.print("Enter check-in date (YYYY-MM-DD)> ");
        LocalDate checkInDate = LocalDate.parse(scanner.nextLine());

        System.out.print("Enter check-out date (YYYY-MM-DD)> ");
        LocalDate checkOutDate = LocalDate.parse(scanner.nextLine());

        int numberOfRooms = 0;
        // Step 2: Specify number of rooms
        while (true) {
            System.out.print("Enter the number of rooms to reserve> ");
            numberOfRooms = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            // Check if the number of rooms is valid
            if (numberOfRooms <= 0) {
                System.out.println("Please enter a valid number of rooms to reserve (greater than 0).");
            } else {
                break; // Exit the loop if a valid number is entered
            }
        }

        // Step 3: Check available rooms based on the selected dates
        List<Room> availableRooms = roomSessionBean.retrieveAvailableRooms(); // You may want to filter these by date
        if (availableRooms.size() < numberOfRooms) {
            System.out.println("Sorry, not enough rooms are available for the selected dates.");
            return;
        }

        // Step 4: Reserve rooms and calculate total amount based on the published rate
        double totalAmount = 0;
        List<Reservation> reservations = new ArrayList<>();
        for (int i = 0; i < numberOfRooms; i++) {
            Room room = availableRooms.get(i);

            // Retrieve published rate for the room type
            Rate publishedRate = rateSessionBean.retrievePublishedRateForRoomType(room.getRoomType().getRoomTypeId());
            if (publishedRate == null) {
                System.out.println("No published rate available for room type: " + room.getRoomType().getName());
                return;
            }

            // Calculate the amount based on the published rate and add to the total
            double reservationAmount = publishedRate.getRatePerNight() * (checkOutDate.toEpochDay() - checkInDate.toEpochDay());
            totalAmount += reservationAmount;

            // Create reservation for each room
            Reservation reservation = new Reservation();
            reservation.setCheckInDate(checkInDate);
            reservation.setCheckOutDate(checkOutDate);
            reservation.setNumberOfGuests(1); // Assuming one guest per room for walk-in
            reservation.setRoomType(room.getRoomType());
            reservation.setReservationType(reservationType.WALK_IN);

            reservations.add(reservation);
        }

        // Step 5: Handle same-day check-in after 2 AM for immediate room allocation
        LocalTime currentTime = LocalTime.now();
        boolean immediateAllocation = checkInDate.equals(LocalDate.now()) && currentTime.isAfter(LocalTime.of(2, 0));
        if (immediateAllocation) {
            for (Reservation reservation : reservations) {
                roomAllocationSessionBean.allocateRoomForReservation(reservation);
            }
        }

        // Step 6: Persist reservations and display confirmation
        for (Reservation reservation : reservations) {
            reservationSessionBean.createReservation(reservation);
        }

        System.out.println("Reservation(s) successfully created for walk-in guest.");
        System.out.printf("Total reservation amount: $%.2f\n", totalAmount);
        if (immediateAllocation) {
            System.out.println("Rooms have been allocated immediately due to same-day check-in after 2 AM.");
        }
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

        // Check if a room has already been allocated for this reservation
        Room allocatedRoom = reservation.getAllocatedRoom();
        if (allocatedRoom != null) {
            // Mark the room as occupied (unavailable for further reservations)
            allocatedRoom.setStatus(roomStatus.UNAVAILABLE);
            roomSessionBean.updateRoom(allocatedRoom);

            System.out.println("Guest checked into Room " + allocatedRoom.getRoomNumber() + ".");
            System.out.println("Check-in successful!");
        } else {
            // Room allocation exception handling
            System.out.println("No room has been allocated for this reservation.");
            System.out.println("This requires manual handling by front-desk staff.");

            // Log the exception for manual handling
            try {
                roomAllocationSessionBean.handleManualRoomAllocationException(reservationId, "Manual handling required: No room allocated.");
                System.out.println("Room allocation exception logged successfully for manual handling.");
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

        // Check if a room has been allocated to this reservation
        Room allocatedRoom = reservation.getAllocatedRoom();
        if (allocatedRoom != null) {
            // Mark the room as available after guest check-out
            allocatedRoom.setStatus(roomStatus.AVAILABLE);
            roomSessionBean.updateRoom(allocatedRoom);

            System.out.println("Guest checked out from Room " + allocatedRoom.getRoomNumber() + ".");
            System.out.println("Check-out successful!");
        } else {
            // If no room was allocated, indicate that the reservation did not have an assigned room
            System.out.println("No room was allocated to this reservation. Check-out cannot be processed.");
        }
    }

}
