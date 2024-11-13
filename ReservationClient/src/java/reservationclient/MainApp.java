package reservationclient;

import ejb.session.stateless.*;
import entity.Guest;
import entity.Rate;
import entity.Reservation;
import entity.Room;
import entity.RoomType;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import util.enumeration.reservationType;

public class MainApp {
    private GuestSessionBeanRemote guestSessionBean;
    private RoomTypeSessionBeanRemote roomTypeSessionBean;
    private RoomSessionBeanRemote roomSessionBean;
    private RateSessionBeanRemote rateSessionBean;
    private ReservationSessionBeanRemote reservationSessionBean;
    private RoomAllocationSessionBeanRemote roomAllocationSessionBean;

    private Guest loggedInGuest;
    private Scanner scanner = new Scanner(System.in);

    public MainApp(GuestSessionBeanRemote guestSessionBean, RoomTypeSessionBeanRemote roomTypeSessionBean,
                   RoomSessionBeanRemote roomSessionBean, RateSessionBeanRemote rateSessionBean,
                   ReservationSessionBeanRemote reservationSessionBean, RoomAllocationSessionBeanRemote roomAllocationSessionBean) {
        this.guestSessionBean = guestSessionBean;
        this.roomTypeSessionBean = roomTypeSessionBean;
        this.roomSessionBean = roomSessionBean;
        this.rateSessionBean = rateSessionBean;
        this.reservationSessionBean = reservationSessionBean;
        this.roomAllocationSessionBean = roomAllocationSessionBean;
    }

    public void run() {
        boolean exit = false;
        while (!exit) {
            if (loggedInGuest == null) {
                showMainMenu();
            } else {
                showMainMenu();
            }
        }
    }

    private void showInitialMenu() {
        System.out.println("*** HoRS Reservation Client :: Main Menu ***");
        System.out.println("1: Search Room Availability");
        System.out.println("2: Login");
        System.out.println("3: Register");
        System.out.println("4: Exit");
        System.out.print("Select an option> ");

        int response = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        switch (response) {
            case 1:
                doSearchRoomAvailability();
                break;
            case 2:
                doGuestLogin();
                break;
            case 3:
                doRegisterGuest();
                break;
            case 4:
                System.out.println("Thank you for using the Reservation Client. Goodbye!");
                System.exit(0);
                break;
            default:
                System.out.println("Invalid option. Please try again.");
        }
    }

    private void doGuestLogin() {
        System.out.println("*** HoRS Reservation Client :: Guest Login ***");
        System.out.print("Enter username: ");
        String username = scanner.nextLine().trim();
        System.out.print("Enter password: ");
        String password = scanner.nextLine().trim();

        loggedInGuest = guestSessionBean.loginGuest(username, password);
        if (loggedInGuest != null) {
            System.out.println("Login successful! Welcome, " + loggedInGuest.getUsername() + ".");
        } else {
            System.out.println("Invalid username or password. Please try again.");
        }
    }

    private void showMainMenu() {
        System.out.println("*** Main Menu ***");
        System.out.println("1: Search Room Availability");
        System.out.println("2: Make a Reservation");
        System.out.println("3: View All Reservations");
        System.out.println("4: View Reservation Details");
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
                doViewReservations();
                break;
            case 4:
                doViewReservationDetails();
                break;
            case 5:
                loggedInGuest = null;
                System.out.println("Logged out successfully.");
                break;
            default:
                System.out.println("Invalid option. Please try again.");
        }
    }
    
    private void doRegisterGuest() {
        System.out.println("*** HoRS Reservation Client :: Register as Guest ***");

        System.out.print("Enter first name: ");
        String firstName = scanner.nextLine().trim();

        System.out.print("Enter last name: ");
        String lastName = scanner.nextLine().trim();

        System.out.print("Enter email: ");
        String email = scanner.nextLine().trim();

        System.out.print("Enter phone number: ");
        String phoneNumber = scanner.nextLine().trim();

        System.out.print("Enter passport number: ");
        String passportNumber = scanner.nextLine().trim();

        System.out.print("Enter username: ");
        String username = scanner.nextLine().trim();

        System.out.print("Enter password: ");
        String password = scanner.nextLine().trim();

        // Create a new Guest object
        Guest newGuest = new Guest(firstName, lastName, email, phoneNumber, passportNumber, username, password);

        try {
            // Register the guest
            guestSessionBean.registerGuest(newGuest);
            System.out.println("Registration successful! You can now log in.");
        } catch (IllegalArgumentException ex) {
            System.out.println("Registration failed: " + ex.getMessage());
        }
    }

    private void doMakeReservation() {
        System.out.println("*** HoRS Reservation Client :: Make a Reservation ***");

        try {
            // Step 1: Get and validate check-in and check-out dates
            System.out.print("Enter check-in date (YYYY-MM-DD)> ");
            LocalDate checkInDate = LocalDate.parse(scanner.nextLine());

            System.out.print("Enter check-out date (YYYY-MM-DD)> ");
            LocalDate checkOutDate = LocalDate.parse(scanner.nextLine());

            if (checkOutDate.isBefore(checkInDate)) {
                System.out.println("Check-out date must be after check-in date.");
                return;
            }

            // Step 2: Search and display available room types with rates
            List<RoomType> availableRoomTypes = roomTypeSessionBean.retrieveAvailableRoomTypes(checkInDate, checkOutDate);
            if (availableRoomTypes.isEmpty()) {
                System.out.println("No room types are available.");
                return;
            }

            System.out.println("\nAvailable Room Types:");
            for (int i = 0; i < availableRoomTypes.size(); i++) {
                RoomType roomType = availableRoomTypes.get(i);
                Rate rate = rateSessionBean.retrieveApplicableRate(roomType, checkInDate);
                double ratePerNight = (rate != null) ? rate.getRatePerNight() : 0.0;
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
                // Retrieve applicable rate for this room type
                Rate rate = rateSessionBean.retrieveApplicableRate(selectedRoomType, checkInDate);
                if (rate == null) {
                    System.out.println("No applicable rate available for room type: " + selectedRoomType.getName());
                    return;
                }

                // Calculate reservation amount
                double reservationAmount = rate.getRatePerNight() * numberOfNights;
                totalAmount += reservationAmount;

                // Create reservation
                Reservation reservation = new Reservation();
                reservation.setCheckInDate(checkInDate);
                reservation.setCheckOutDate(checkOutDate);
                reservation.setNumberOfGuests(numberOfGuests);
                reservation.setRoomType(selectedRoomType);
                reservation.setReservationType(reservationType.ONLINE);
                reservation.setGuest(loggedInGuest);

                Long reservationId = reservationSessionBean.createOnlineReservation(reservation, checkInDate, checkOutDate);
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
    
    private void doViewReservations() {
        System.out.println("*** HoRS Reservation Client :: View My Reservations ***");

        try {
            // Retrieve reservations for the logged-in guest
            List<Reservation> reservations = guestSessionBean.viewReservations(loggedInGuest.getGuestId());

            if (reservations.isEmpty()) {
                System.out.println("You have no reservations.");
                return;
            }

            // Display each reservation
            System.out.println("\nYour Reservations:");
            for (Reservation reservation : reservations) {
                System.out.println("Reservation ID: " + reservation.getReservationId());
                System.out.println("Check-in Date: " + reservation.getCheckInDate());
                System.out.println("Check-out Date: " + reservation.getCheckOutDate());
                System.out.println("Room Type: " + reservation.getRoomType().getName());
                System.out.println("Number of Guests: " + reservation.getNumberOfGuests());
                System.out.printf("Total Amount: $%.2f\n", reservation.getTotalAmount());
                System.out.println("Reservation Status: " + reservation.getReservationType());
                System.out.println("---------------------------------------");
            }

        } catch (Exception ex) {
            System.out.println("An error occurred while retrieving your reservations. Please try again.");
        }
    }
    
    private void doSearchRoomAvailability() {
        System.out.println("*** HoRS Reservation Client :: Search Room Availability ***");

        // Step 1: Get check-in and check-out dates
        System.out.print("Enter check-in date (YYYY-MM-DD)> ");
        LocalDate checkInDate = LocalDate.parse(scanner.nextLine());

        System.out.print("Enter check-out date (YYYY-MM-DD)> ");
        LocalDate checkOutDate = LocalDate.parse(scanner.nextLine());

        // Step 2: Retrieve available rooms based on date range
        List<Room> availableRooms = roomSessionBean.retrieveAvailableRoomsForDates(checkInDate, checkOutDate);

        if (availableRooms.isEmpty()) {
            System.out.println("No rooms are available for the selected dates.");
            return;
        }

        // Step 3: Display available rooms and rates
        System.out.println("\nAvailable Rooms:");
        for (Room room : availableRooms) {
            RoomType roomType = room.getRoomType();

            // Retrieve the published rate for this room type
            Rate publishedRate = rateSessionBean.retrievePublishedRateForRoomType(roomType.getRoomTypeId());

            if (publishedRate != null) {
                // Calculate the total cost based on the stay duration
                long nights = checkOutDate.toEpochDay() - checkInDate.toEpochDay();
                double totalCost = publishedRate.getRatePerNight() * nights;

                System.out.printf("Room ID: %d, Room Type: %s, Rate Per Night: $%.2f, Total Cost: $%.2f\n",
                    room.getRoomId(), roomType.getName(), publishedRate.getRatePerNight(), totalCost);
            } else {
                System.out.printf("Room ID: %d, Room Type: %s, Rate information not available.\n",
                    room.getRoomId(), roomType.getName());
            }
        }
    }
    
    private void doViewReservationDetails() {
        System.out.println("*** View Reservation Details ***");

        // Prompt for reservation ID
        System.out.print("Enter Reservation ID> ");
        Long reservationId = scanner.nextLong();
        scanner.nextLine(); // Consume newline

        // Retrieve reservation by ID
        Reservation reservation = reservationSessionBean.retrieveReservationById(reservationId);

        if (reservation == null || !reservation.getGuest().getGuestId().equals(loggedInGuest.getGuestId())) {
            System.out.println("Reservation not found or you do not have access to this reservation.");
            return;
        }

        // Display reservation details
        System.out.println("\nReservation Details:");
        System.out.printf("Reservation ID: %d\n", reservation.getReservationId());
        System.out.printf("Check-In Date: %s\n", reservation.getCheckInDate());
        System.out.printf("Check-Out Date: %s\n", reservation.getCheckOutDate());
        System.out.printf("Room Type: %s\n", reservation.getRoomType().getName());
        System.out.printf("Number of Guests: %d\n", reservation.getNumberOfGuests());
        System.out.printf("Reservation Type: %s\n", reservation.getReservationType());
        System.out.printf("Total Amount: $%.2f\n", reservation.getTotalAmount());

        if (reservation.getRoomAllocation() != null) {
            System.out.printf("Allocated Room ID: %d\n", reservation.getRoomAllocation().getRoom().getRoomId());
        } else {
            System.out.println("Room allocation pending.");
        }
    }

}
