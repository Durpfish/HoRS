package hotelreservationsystemjavaseclient;

import ws.holiday.HotelWebService_Service;
import ws.holiday.Partner;
import ws.holiday.Reservation;
import ws.holiday.Room;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import ws.holiday.RoomType;

public class HotelReservationSystemJavaSeClient {

    private static HotelWebService_Service service = new HotelWebService_Service();
    private static Partner loggedInPartner = null;
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        run();
    }

    private static void run() {
        boolean exit = false;

        while (!exit) {
            if (loggedInPartner == null) {
                showInitialMenu();
            } else {
                showMainMenu();
            }
        }
    }

    private static void showInitialMenu() {
        System.out.println("*** HoRS Partner Client :: Main Menu ***");
        System.out.println("1: Search Room Availability");
        System.out.println("2: Login");
        System.out.println("3: Exit");
        System.out.print("Select an option> ");

        int response = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        switch (response) {
            case 1:
                doSearchRoomAvailability();
                break;
            case 2:
                doPartnerLogin();
                break;
            case 3:
                System.out.println("Thank you for using the Partner Client. Goodbye!");
                System.exit(0);
                break;
            default:
                System.out.println("Invalid option. Please try again.");
        }
    }

    private static void doPartnerLogin() {
        System.out.println("*** HoRS Partner Client :: Partner Login ***");
        System.out.print("Enter username: ");
        String username = scanner.nextLine().trim();
        System.out.print("Enter password: ");
        String password = scanner.nextLine().trim();

        loggedInPartner = service.getHotelWebServicePort().loginPartner(username, password);

        if (loggedInPartner != null) {
            System.out.println("Login successful! Welcome, " + loggedInPartner.getName());
        } else {
            System.out.println("Invalid username or password.");
        }
    }

    private static void showMainMenu() {
        System.out.println("*** Main Menu ***");
        System.out.println("1: Search Room Availability");
        System.out.println("2: Make a Reservation");
        System.out.println("3: View Reservation Details");
        System.out.println("4: View All Reservations");
        System.out.println("5: Logout");
        System.out.print("Select an option> ");

        int response = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        switch (response) {
            case 1:
                doSearchRoomAvailability();
                break;
            case 2:
                doMakeReservation();
                break;
            case 3:
                doViewReservationDetails();
                break;
            case 4:
                doViewAllReservations();
                break;
            case 5:
                loggedInPartner = null;
                System.out.println("Logged out successfully.");
                break;
            default:
                System.out.println("Invalid option. Please try again.");
        }
    }
    
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");



    private static void doSearchRoomAvailability() {
        System.out.println("*** Partner Client :: Search Room Availability ***");

        System.out.print("Enter check-in date (YYYY-MM-DD)> ");
        String checkInDateStr = scanner.nextLine();
        System.out.print("Enter check-out date (YYYY-MM-DD)> ");
        String checkOutDateStr = scanner.nextLine();

        // Call the web service with date strings
        List<Room> availableRooms = service.getHotelWebServicePort().retrieveAvailableRoomsForDates(checkInDateStr, checkOutDateStr);

        if (availableRooms.isEmpty()) {
            System.out.println("No rooms available for the selected dates.");
        } else {
            System.out.println("Available Rooms:");
            for (Room room : availableRooms) {
                System.out.println("Room ID: " + room.getRoomId() + ", Room Type: " + room.getRoomType().getName());
            }
        }
    }

    private static void doMakeReservation() {
        System.out.println("*** Partner Client :: Make a Reservation ***");

        try {
            // Step 1: Get and validate check-in and check-out dates
            System.out.print("Enter check-in date (YYYY-MM-DD)> ");
            String checkInDateStr = scanner.nextLine();
            System.out.print("Enter check-out date (YYYY-MM-DD)> ");
            String checkOutDateStr = scanner.nextLine();

            LocalDate checkInDate = LocalDate.parse(checkInDateStr);
            LocalDate checkOutDate = LocalDate.parse(checkOutDateStr);

            if (checkOutDate.isBefore(checkInDate)) {
                System.out.println("Check-out date must be after check-in date.");
                return;
            }

            // Step 2: Search and display available room types with rates
            List<RoomType> availableRoomTypes = service.getHotelWebServicePort().retrieveAvailableRoomTypes(checkInDateStr, checkOutDateStr);
            if (availableRoomTypes.isEmpty()) {
                System.out.println("No room types are available.");
                return;
            }

            System.out.println("\nAvailable Room Types:");
            for (int i = 0; i < availableRoomTypes.size(); i++) {
                RoomType roomType = availableRoomTypes.get(i);
                double ratePerNight = service.getHotelWebServicePort().retrieveApplicableRate(roomType.getRoomTypeId(), checkInDateStr);
                System.out.printf("%d. %s - Rate per night: $%.2f\n", i + 1, roomType.getName(), ratePerNight);
            }

            System.out.print("Select a room type by number> ");
            int roomTypeSelection = scanner.nextInt() - 1;
            scanner.nextLine(); // Consume newline

            if (roomTypeSelection < 0 || roomTypeSelection >= availableRoomTypes.size()) {
                System.out.println("Invalid room type selection.");
                return;
            }

            RoomType selectedRoomType = availableRoomTypes.get(roomTypeSelection);

            // Step 3: Confirm the number of rooms and guests
            System.out.print("Enter the number of rooms to reserve> ");
            int numberOfRooms = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            if (numberOfRooms <= 0) {
                System.out.println("Invalid number of rooms.");
                return;
            }

            System.out.print("Enter the number of guests per room> ");
            int numberOfGuests = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            if (numberOfGuests <= 0) {
                System.out.println("Invalid number of guests.");
                return;
            }

            // Step 4: Calculate total amount and create reservations
            double totalAmount = 0.0;
            long numberOfNights = checkOutDate.toEpochDay() - checkInDate.toEpochDay();
            List<Long> reservationIds = new ArrayList<>();

            for (int i = 0; i < numberOfRooms; i++) {
                double ratePerNight = service.getHotelWebServicePort().retrieveApplicableRate(selectedRoomType.getRoomTypeId(), checkInDateStr);
                if (ratePerNight == 0.0) {
                    System.out.println("No applicable rate available for room type: " + selectedRoomType.getName());
                    return;
                }

                // Calculate reservation amount
                double reservationAmount = ratePerNight * numberOfNights;
                totalAmount += reservationAmount;

                // Create reservation using web service with individual parameters
                Long reservationId = service.getHotelWebServicePort().createReservationWithDates(
                        loggedInPartner.getPartnerId(), // Partner ID
                        selectedRoomType.getRoomTypeId(), // Room Type ID
                        checkInDateStr, // Check-in date as String
                        checkOutDateStr, // Check-out date as String
                        numberOfGuests // Number of guests per room
                );
                reservationIds.add(reservationId);
            }

            // Step 5: Display reservation summary
            System.out.println("\nReservation Summary:");
            System.out.printf("Total number of rooms reserved: %d\n", numberOfRooms);
            System.out.printf("Total amount: $%.2f\n", totalAmount);
            System.out.println("Reservation IDs: " + reservationIds);

        } catch (Exception ex) {
            System.out.println("An error occurred while making the reservation. Please try again.");
        }
    }

    


    private static void doViewReservationDetails() {
        System.out.println("*** Partner Client :: View Reservation Details ***");

        System.out.print("Enter Reservation ID> ");
        Long reservationId = scanner.nextLong();
        scanner.nextLine(); // Consume newline

        Reservation reservation = service.getHotelWebServicePort().retrieveReservationById(reservationId);

        if (reservation == null) {
            System.out.println("No reservation found with the provided ID.");
        } else {
            System.out.println("Reservation Details:");
            System.out.println("Reservation ID: " + reservation.getReservationId());
            System.out.println("Check-in Date: " + reservation.getCheckInDateFormatted());
            System.out.println("Check-out Date: " + reservation.getCheckOutDateFormatted());
            if (reservation.getRoomType() != null) {
                System.out.println("Room Type: " + reservation.getRoomType().getName());
            } else {
                System.out.println("Room Type information is not available.");
            }
            System.out.println("Total Amount: $" + reservation.getTotalAmount());
        }
    }

    private static void doViewAllReservations() {
        System.out.println("*** Partner Client :: View All Reservations ***");

        List<Reservation> reservations = service.getHotelWebServicePort().retrievePartnerReservations(loggedInPartner.getPartnerId());

        if (reservations.isEmpty()) {
            System.out.println("No reservations found for your partner account.");
        } else {
            System.out.println("All Reservations:");
            for (Reservation reservation : reservations) {
                System.out.println("Reservation ID: " + reservation.getReservationId() +
                        ", Check-in Date: " + reservation.getCheckInDateFormatted() +
                        ", Check-out Date: " + reservation.getCheckOutDateFormatted());
            }
        }
    }
}
