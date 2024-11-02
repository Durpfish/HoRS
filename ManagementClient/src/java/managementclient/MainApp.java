package managementclient;

import ejb.session.stateless.*;
import entity.*;
import util.enumeration.employeeRole;
import util.enumeration.rateType;
import util.enumeration.roomStatus;
import util.enumeration.reservationType;

import javax.ejb.EJB;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class MainApp {

    private EmployeeSessionBeanRemote employeeSessionBean;
    private PartnerSessionBeanRemote partnerSessionBean;
    private RoomTypeSessionBeanRemote roomTypeSessionBean;
    private RoomSessionBeanRemote roomSessionBean;
    private RateSessionBeanRemote rateSessionBean;
    private ReservationSessionBeanRemote reservationSessionBean;
    private RoomAllocationSessionBeanRemote roomAllocationSessionBean;

    private Employee loggedInEmployee;
    
    private Scanner scanner = new Scanner(System.in);
    
        
    public MainApp() {
    }
    
    public MainApp(EmployeeSessionBeanRemote employeeSessionBean, PartnerSessionBeanRemote partnerSessionBean, RoomTypeSessionBeanRemote roomTypeSessionBean, RoomSessionBeanRemote roomSessionBean, RateSessionBeanRemote rateSessionBean, ReservationSessionBeanRemote reservationSessionBean, RoomAllocationSessionBeanRemote roomAllocationSessionBean) {
        this.employeeSessionBean = employeeSessionBean;
        this.partnerSessionBean = partnerSessionBean;
        this.roomTypeSessionBean = roomTypeSessionBean;
        this.roomSessionBean = roomSessionBean;
        this.rateSessionBean = rateSessionBean;
        this.reservationSessionBean = reservationSessionBean;
        this.roomAllocationSessionBean = roomAllocationSessionBean;
    }

    public void run() {
        boolean exit = false;

        while (!exit) {
            login();
            if (loggedInEmployee != null) {
                showMainMenu();
                loggedInEmployee = null; // Reset logged-in employee after logout
            } else {
                System.out.println("Would you like to try logging in again? (Y/N)");
                String retry = scanner.nextLine().trim();
                if (retry.equalsIgnoreCase("N")) {
                    exit = true;
                }
            }
        }

        System.out.println("Thank you for using the Hotel Reservation System. Goodbye!");
    }

    private void login() {
        System.out.println("*** HoRS Management Client :: Login ***");
        System.out.print("Enter username: ");
        String username = scanner.nextLine().trim();
        System.out.print("Enter password: ");
        String password = scanner.nextLine().trim();

        loggedInEmployee = employeeSessionBean.loginEmployee(username, password);
        if (loggedInEmployee != null) {
            System.out.println("Login successful! Welcome, " + loggedInEmployee.getFirstName() + " " + loggedInEmployee.getLastName() + ".");
            System.out.println("Role: " + loggedInEmployee.getRole());
        } else {
            System.out.println("Invalid username or password.");
        }
    }

    private void showMainMenu() {
        switch (loggedInEmployee.getRole()) {
            case SYSTEM_ADMIN:
                showSystemAdminMenu();
                break;
            case OPERATION_MANAGER:
                showOperationManagerMenu();
                break;
            case SALES_MANAGER:
                showSalesManagerMenu();
                break;
            case GUEST_RELATION_OFFICER:
                showGuestRelationOfficerMenu();
                break;
            default:
                System.out.println("Invalid role");
                break;
        }
    }

    private void showSystemAdminMenu() {
        int choice;
        do {
            System.out.println("\n*** System Administration Module ***");
            System.out.println("1: Create New Employee");
            System.out.println("2: View All Employees");
            System.out.println("3: Create New Partner");
            System.out.println("4: View All Partners");
            System.out.println("5: Logout");

            System.out.print("Enter choice: ");
            while (!scanner.hasNextInt()) { // Check for valid integer input
                System.out.println("Invalid choice. Please enter a number.");
                scanner.next(); // Clear invalid input
            }
            choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    doCreateNewEmployee();
                    break;
                case 2:
                    doViewAllEmployees();
                    break;
                case 3:
                    doCreateNewPartner();
                    break;
                case 4:
                    doViewAllPartners();
                    break;
                case 5:
                    System.out.println("Logging out...");
                    break;
                default:
                    System.out.println("Invalid choice. Try again.");
            }
        } while (choice != 5);
    }

    private void showOperationManagerMenu() {
        int choice;
        do {
            System.out.println("\n*** Hotel Operation Module ***");
            System.out.println("1: Create New Room Type");
            System.out.println("2: View Room Type Details");
            System.out.println("3: Update Room Type");
            System.out.println("4: Delete Room Type");
            System.out.println("5: View All Room Types");
            System.out.println("6: Create New Room");
            System.out.println("7: Update Room");
            System.out.println("8: Delete Room");
            System.out.println("9: View All Rooms");
            System.out.println("10: View Room Allocation Exception Report");
            System.out.println("11: Logout");

            System.out.print("Enter choice: ");
            choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    doCreateNewRoomType(scanner);
                    break;
                case 2:
                    doViewRoomTypeDetails(scanner);
                    break;
                case 3:
                    doUpdateRoomType();
                    break;
                case 4:
                    doDeleteRoomType();
                    break;
                case 5:
                    doViewAllRoomTypes();
                    break;
                case 6:
                    doCreateNewRoom();
                    break;
                case 7:
                    doUpdateRoom();
                    break;
                case 8:
                    doDeleteRoom();
                    break;
                case 9:
                    doViewAllRooms();
                    break;
                case 10:
                    doViewRoomAllocationExceptionReport();
                    break;
                case 11:
                    System.out.println("Logging out...");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        } while (choice != 11);
    }

    private void showSalesManagerMenu() {
        int choice;
        do {
            System.out.println("\n*** Sales Management Module ***");
            System.out.println("1: Create New Room Rate");
            System.out.println("2: View Room Rate Details");
            System.out.println("3: Update Room Rate");
            System.out.println("4: Delete Room Rate");
            System.out.println("5: View All Room Rates");
            System.out.println("6: Logout");

            System.out.print("Enter choice: ");
            choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    doCreateNewRoomRate();
                    break;
                case 2:
                    doViewRoomRateDetails();
                    break;
                case 3:
                    doUpdateRoomRate();
                    break;
                case 4:
                    doDeleteRoomRate();
                    break;
                case 5:
                    doViewAllRoomRates();
                    break;
                case 6:
                    System.out.println("Logging out...");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        } while (choice != 6);
    }    

    private void showGuestRelationOfficerMenu() {
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

    private void doCreateNewEmployee() {
        System.out.print("Enter employee first name> ");
        String firstName = scanner.nextLine();
        System.out.print("Enter employee last name> ");
        String lastName = scanner.nextLine();
        System.out.print("Enter employee username> ");
        String username = scanner.nextLine();
        System.out.print("Enter employee password> ");
        String password = scanner.nextLine();

        employeeRole role = null;
        boolean validRole = false;
        while (!validRole) {
            System.out.println("Select employee role: 1: SYSTEM_ADMIN, 2: OPERATION_MANAGER, 3: SALES_MANAGER, 4: GUEST_RELATION_OFFICER");
            System.out.print("Enter choice: ");
            if (scanner.hasNextInt()) {
                int roleOption = scanner.nextInt();
                scanner.nextLine(); // Consume newline
                if (roleOption >= 1 && roleOption <= 4) {
                    role = employeeRole.values()[roleOption - 1];
                    validRole = true;
                } else {
                    System.out.println("Invalid role choice. Please select a number between 1 and 4.");
                }
            } else {
                System.out.println("Invalid input. Please enter a number between 1 and 4.");
                scanner.next(); // Clear invalid input
            }
        }

        Employee employee = new Employee();
        employee.setFirstName(firstName);
        employee.setLastName(lastName);
        employee.setUsername(username);
        employee.setPassword(password);
        employee.setRole(role);

        employeeSessionBean.addEmployee(employee);
        System.out.println("Employee created successfully!");
    }

    private void doViewAllEmployees() {
        List<Employee> employees = employeeSessionBean.getAllEmployees();
        System.out.println("*** List of All Employees ***");
        for (Employee employee : employees) {
            System.out.println("ID: " + employee.getEmployeeId() + ", Name: " + employee.getFirstName() + " " + employee.getLastName() + ", Role: " + employee.getRole());
        }
    }
    
    private void doViewRoomAllocationExceptionReport() {
        List<RoomAllocationExceptionReport> exceptionReports = roomAllocationSessionBean.viewAllRoomAllocationExceptions();

        System.out.println("*** Room Allocation Exception Report ***");
        if (exceptionReports.isEmpty()) {
            System.out.println("No room allocation exceptions found.");
        } else {
            for (RoomAllocationExceptionReport report : exceptionReports) {
                System.out.println("Report ID: " + report.getReportId());
                System.out.println("Reservation ID: " + report.getReservation().getReservationId());
                System.out.println("Message: " + report.getMessage());
                System.out.println("Exception Date: " + report.getExceptionDate());
                System.out.println("-----------------------------------------");
            }
        }
    }
    
    private void doCreateNewPartner() {
        System.out.println("*** Create New Partner ***");

        System.out.print("Enter partner name> ");
        String name = scanner.nextLine();

        System.out.print("Enter contact person> ");
        String contactPerson = scanner.nextLine();

        System.out.print("Enter partner email> ");
        String email = scanner.nextLine();

        System.out.print("Enter partner phone number> ");
        String phone = scanner.nextLine();

        System.out.print("Enter partner username> ");
        String username = scanner.nextLine();

        System.out.print("Enter partner password> ");
        String password = scanner.nextLine();

        // Create a new Partner instance and populate its fields
        Partner partner = new Partner();
        partner.setName(name);
        partner.setContactPerson(contactPerson);
        partner.setEmail(email);
        partner.setPhone(phone);
        partner.setUsername(username);
        partner.setPassword(password);

        // Register the partner using the session bean
        partnerSessionBean.registerPartner(partner);
        System.out.println("Partner created successfully!");
    }

    
    private void doViewAllPartners() {
        System.out.println("*** View All Partners ***");
        List<Partner> partners = partnerSessionBean.getAllPartners();

        if (partners.isEmpty()) {
            System.out.println("No partners found.");
        } else {
            for (Partner partner : partners) {
                System.out.println("ID: " + partner.getPartnerId());
                System.out.println("Name: " + partner.getName());
                System.out.println("Contact Person: " + partner.getContactPerson());
                System.out.println("Email: " + partner.getEmail());
                System.out.println("Phone: " + partner.getPhone());
                System.out.println("Username: " + partner.getUsername());
                System.out.println("-----------------------------------------");
            }
        }
    }
    
    private void doCreateNewRoomType(Scanner scanner) {
        System.out.println("*** Create New Room Type ***");

        System.out.print("Enter room type name> ");
        String name = scanner.nextLine();

        System.out.print("Enter description> ");
        String description = scanner.nextLine();

        System.out.print("Enter capacity> ");
        int capacity = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        System.out.print("Enter size (in square meters)> ");
        double size = scanner.nextDouble();
        scanner.nextLine(); // Consume newline

        System.out.print("Enter bed type (e.g., Queen, King)> ");
        String bedType = scanner.nextLine();

        System.out.print("Enter amenities (comma-separated)> ");
        String amenities = scanner.nextLine();

        System.out.print("Enter hierarchy of this room type> ");
        int order = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        RoomType roomType = new RoomType();
        roomType.setName(name);
        roomType.setDescription(description);
        roomType.setCapacity(capacity);
        roomType.setSize(size);
        roomType.setBedType(bedType);
        roomType.setAmenities(amenities);
        roomType.setRoomOrder(order);

        roomTypeSessionBean.createRoomType(roomType);
        System.out.println("Room type created successfully!");
    }
    
    private void doViewRoomTypeDetails(Scanner scanner) {
        System.out.println("*** View Room Type Details ***");
        System.out.print("Enter room type ID> ");
        Long roomTypeId = scanner.nextLong();
        scanner.nextLine(); 

        RoomType roomType = roomTypeSessionBean.retrieveRoomTypeById(roomTypeId);

        if (roomType != null) {
            System.out.println("Room Type ID: " + roomType.getRoomTypeId());
            System.out.println("Name: " + roomType.getName());
            System.out.println("Description: " + roomType.getDescription());
            System.out.println("Capacity: " + roomType.getCapacity());
            System.out.println("Size: " + roomType.getSize());
            System.out.println("Bed Type: " + roomType.getBedType());
            System.out.println("Amenities: " + roomType.getAmenities());
            System.out.println("Order: " + roomType.getRoomOrder());
            System.out.println("Disabled: " + (roomType.isDisabled() ? "Yes" : "No"));
        } else {
            System.out.println("Room type with ID " + roomTypeId + " does not exist.");
        }
    }

    private void doUpdateRoomType() {
        System.out.println("*** Update Room Type ***");

        System.out.print("Enter Room Type ID to update> ");
        Long roomTypeId = scanner.nextLong();
        scanner.nextLine(); // Consume newline

        RoomType roomType = roomTypeSessionBean.retrieveRoomTypeById(roomTypeId);
        if (roomType == null) {
            System.out.println("Room Type ID not found.");
            return;
        }

        System.out.print("Enter new name (or press Enter to keep current: " + roomType.getName() + ")> ");
        String name = scanner.nextLine();
        if (!name.isEmpty()) {
            roomType.setName(name);
        }

        System.out.print("Enter new description (or press Enter to keep current: " + roomType.getDescription() + ")> ");
        String description = scanner.nextLine();
        if (!description.isEmpty()) {
            roomType.setDescription(description);
        }

        System.out.print("Enter new capacity (or 0 to keep current: " + roomType.getCapacity() + ")> ");
        int capacity = scanner.nextInt();
        scanner.nextLine(); // Consume newline
        if (capacity != 0) {
            roomType.setCapacity(capacity);
        }

        System.out.print("Enter new size (or 0 to keep current: " + roomType.getSize() + ")> ");
        double size = scanner.nextDouble();
        scanner.nextLine(); // Consume newline
        if (size != 0) {
            roomType.setSize(size);
        }

        System.out.print("Enter new bed type (or press Enter to keep current: " + roomType.getBedType() + ")> ");
        String bedType = scanner.nextLine();
        if (!bedType.isEmpty()) {
            roomType.setBedType(bedType);
        }

        System.out.print("Enter new amenities (or press Enter to keep current: " + roomType.getAmenities() + ")> ");
        String amenities = scanner.nextLine();
        if (!amenities.isEmpty()) {
            roomType.setAmenities(amenities);
        }

        roomTypeSessionBean.updateRoomType(roomType);
        System.out.println("Room Type updated successfully!");
    }

    private void doDeleteRoomType() {
        System.out.println("*** Delete Room Type ***");

        System.out.print("Enter Room Type ID to delete> ");
        Long roomTypeId = scanner.nextLong();
        scanner.nextLine(); // Consume newline

        RoomType roomType = roomTypeSessionBean.retrieveRoomTypeById(roomTypeId);
        if (roomType == null) {
            System.out.println("Room Type ID not found.");
            return;
        }

        System.out.print("Are you sure you want to delete this room type? (Y/N)> ");
        String confirm = scanner.nextLine().trim().toUpperCase();
        if (confirm.equals("Y")) {
            try {
                roomTypeSessionBean.deleteRoomType(roomTypeId);
                System.out.println("Room Type deleted successfully!");
            } catch (Exception e) {
                System.out.println("Unable to delete Room Type. It may be in use.");
            }
        } else {
            System.out.println("Room Type deletion cancelled.");
        }
    }

    private void doViewAllRoomTypes() {
        System.out.println("*** View All Room Types ***");

        List<RoomType> roomTypes = roomTypeSessionBean.retrieveAllRoomTypes();
        System.out.printf("%-10s %-20s %-30s %-10s %-10s %-20s %-30s\n", 
                          "ID", "Name", "Description", "Capacity", "Size", "Bed Type", "Amenities");

        for (RoomType roomType : roomTypes) {
            System.out.printf("%-10d %-20s %-30s %-10d %-10.2f %-20s %-30s\n",
                              roomType.getRoomTypeId(),
                              roomType.getName(),
                              roomType.getDescription(),
                              roomType.getCapacity(),
                              roomType.getSize(),
                              roomType.getBedType(),
                              roomType.getAmenities());
        }
    }

    private void doCreateNewRoom() {
        System.out.println("*** Create New Room ***");

        System.out.print("Enter room number> ");
        String roomNumber = scanner.nextLine();

        System.out.print("Enter floor number> ");
        int floorNumber = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        System.out.println("Select Room Type:");
        List<RoomType> roomTypes = roomTypeSessionBean.retrieveAllRoomTypes();
        for (int i = 0; i < roomTypes.size(); i++) {
            System.out.println((i + 1) + ": " + roomTypes.get(i).getName());
        }

        int roomTypeChoice = scanner.nextInt();
        scanner.nextLine(); // Consume newline
        RoomType roomType = roomTypes.get(roomTypeChoice - 1);

        Room room = new Room();
        room.setRoomNumber(roomNumber);
        room.setFloorNumber(floorNumber);
        room.setRoomType(roomType);
        room.setStatus(roomStatus.AVAILABLE);

        roomSessionBean.createRoom(room);
        System.out.println("Room created successfully!");
    }
    
    
    private void doUpdateRoom() {
       System.out.println("*** Update Room ***");

       System.out.print("Enter Room ID to update> ");
       Long roomId = scanner.nextLong();
       scanner.nextLine(); // Consume newline

       Room room = roomSessionBean.retrieveRoomById(roomId);
       if (room == null) {
           System.out.println("Room ID not found.");
           return;
       }

       System.out.print("Enter new room number (or press Enter to keep current: " + room.getRoomNumber() + ")> ");
       String roomNumber = scanner.nextLine();
       if (!roomNumber.isEmpty()) {
           room.setRoomNumber(roomNumber);
       }

       System.out.print("Enter new floor number (or 0 to keep current: " + room.getFloorNumber() + ")> ");
       int floorNumber = scanner.nextInt();
       scanner.nextLine(); // Consume newline
       if (floorNumber != 0) {
           room.setFloorNumber(floorNumber);
       }

       System.out.println("Select new Room Type (or 0 to keep current: " + room.getRoomType().getName() + ")");
       List<RoomType> roomTypes = roomTypeSessionBean.retrieveAllRoomTypes(); // Retrieve list of room types
       for (int i = 0; i < roomTypes.size(); i++) {
           System.out.println((i + 1) + ": " + roomTypes.get(i).getName());
       }

       int roomTypeChoice = scanner.nextInt();
       scanner.nextLine(); // Consume newline
       if (roomTypeChoice > 0 && roomTypeChoice <= roomTypes.size()) {
           room.setRoomType(roomTypes.get(roomTypeChoice - 1));
       }

       roomSessionBean.updateRoom(room);
       System.out.println("Room updated successfully!");
   }

    private void doDeleteRoom() {
        System.out.println("*** Delete Room ***");

        System.out.print("Enter Room ID to delete> ");
        Long roomId = scanner.nextLong();
        scanner.nextLine(); // Consume newline

        Room room = roomSessionBean.retrieveRoomById(roomId);
        if (room == null) {
            System.out.println("Room ID not found.");
            return;
        }

        System.out.print("Are you sure you want to delete this room? (Y/N)> ");
        String confirm = scanner.nextLine().trim().toUpperCase();
        if (confirm.equals("Y")) {
            try {
                roomSessionBean.deleteRoom(roomId);
                System.out.println("Room deleted successfully!");
            } catch (Exception e) {
                System.out.println("Unable to delete Room. It may be in use.");
            }
        } else {
            System.out.println("Room deletion cancelled.");
        }
    }

    private void doViewAllRooms() {
        System.out.println("*** View All Rooms ***");

        List<Room> rooms = roomSessionBean.retrieveAllRooms();
        System.out.printf("%-10s %-15s %-10s %-20s %-15s\n", 
                          "Room ID", "Room Number", "Floor", "Room Type", "Status");

        for (Room room : rooms) {
            System.out.printf("%-10d %-15s %-10d %-20s %-15s\n",
                              room.getRoomId(),
                              room.getRoomNumber(),
                              room.getFloorNumber(),
                              room.getRoomType().getName(),
                              room.getStatus());
        }
    }

    private void doCreateNewRoomRate() {
        System.out.println("*** Create New Room Rate ***");

        System.out.print("Enter rate per night> ");
        double ratePerNight = scanner.nextDouble();
        scanner.nextLine(); // Consume newline

        System.out.println("Select rate type:");
        for (int i = 0; i < rateType.values().length; i++) {
            System.out.println((i + 1) + ": " + rateType.values()[i]);
        }

        int rateTypeChoice = scanner.nextInt();
        scanner.nextLine(); // Consume newline
        rateType selectedRateType = rateType.values()[rateTypeChoice - 1];

        System.out.print("Enter validity start date (YYYY-MM-DD)> ");
        LocalDate validFrom = LocalDate.parse(scanner.nextLine());

        System.out.print("Enter validity end date (YYYY-MM-DD)> ");
        LocalDate validTo = LocalDate.parse(scanner.nextLine());

        System.out.println("Select Room Type:");
        List<RoomType> roomTypes = roomTypeSessionBean.retrieveAllRoomTypes();
        for (int i = 0; i < roomTypes.size(); i++) {
            System.out.println((i + 1) + ": " + roomTypes.get(i).getName());
        }

        int roomTypeChoice = scanner.nextInt();
        scanner.nextLine(); // Consume newline
        RoomType roomType = roomTypes.get(roomTypeChoice - 1);

        Rate roomRate = new Rate(ratePerNight, validFrom, validTo, false, selectedRateType, roomType);
        rateSessionBean.createRate(roomRate);
        System.out.println("Room rate created successfully!");
    }

    private void doViewRoomRateDetails() {
        System.out.println("*** View Room Rate Details ***");

        System.out.print("Enter Room Rate ID> ");
        Long rateId = scanner.nextLong();
        scanner.nextLine(); // Consume newline

        Rate rate = rateSessionBean.retrieveRateById(rateId);
        if (rate != null) {
            System.out.println("Rate ID: " + rate.getRateId());
            System.out.println("Rate Per Night: $" + rate.getRatePerNight());
            System.out.println("Rate Type: " + rate.getRateType());
            System.out.println("Validity From: " + rate.getValidFrom());
            System.out.println("Validity To: " + rate.getValidTo());
            System.out.println("Room Type: " + rate.getRoomType().getName());
            System.out.println("Status: " + (rate.isDisabled() ? "Disabled" : "Active"));
        } else {
            System.out.println("Room Rate ID not found.");
        }
    }

    private void doUpdateRoomRate() {
        System.out.println("*** Update Room Rate ***");

        System.out.print("Enter Room Rate ID to update> ");
        Long rateId = scanner.nextLong();
        scanner.nextLine(); // Consume newline

        Rate rate = rateSessionBean.retrieveRateById(rateId);
        if (rate == null) {
            System.out.println("Room Rate ID not found.");
            return;
        }

        System.out.print("Enter new rate per night (or 0 to keep current: $" + rate.getRatePerNight() + ")> ");
        double ratePerNight = scanner.nextDouble();
        scanner.nextLine(); // Consume newline
        if (ratePerNight > 0) {
            rate.setRatePerNight(ratePerNight);
        }

        System.out.println("Select new rate type (or press 0 to keep current: " + rate.getRateType() + ")");
        for (int i = 0; i < rateType.values().length; i++) {
            System.out.println((i + 1) + ": " + rateType.values()[i]);
        }
        int rateTypeChoice = scanner.nextInt();
        scanner.nextLine(); // Consume newline
        if (rateTypeChoice > 0 && rateTypeChoice <= rateType.values().length) {
            rate.setRateType(rateType.values()[rateTypeChoice - 1]);
        }

        System.out.print("Enter new validity start date (or press Enter to keep current: " + rate.getValidFrom() + ")> ");
        String validFromInput = scanner.nextLine();
        if (!validFromInput.isEmpty()) {
            LocalDate validFrom = LocalDate.parse(validFromInput);
            rate.setValidFrom(validFrom);
        }

        System.out.print("Enter new validity end date (or press Enter to keep current: " + rate.getValidTo() + ")> ");
        String validToInput = scanner.nextLine();
        if (!validToInput.isEmpty()) {
            LocalDate validTo = LocalDate.parse(validToInput);
            rate.setValidTo(validTo);
        }

        rateSessionBean.updateRate(rate);
        System.out.println("Room rate updated successfully!");
    }

    private void doDeleteRoomRate() {
        System.out.println("*** Delete Room Rate ***");

        System.out.print("Enter Room Rate ID to delete> ");
        Long rateId = scanner.nextLong();
        scanner.nextLine(); // Consume newline

        Rate rate = rateSessionBean.retrieveRateById(rateId);
        if (rate == null) {
            System.out.println("Room Rate ID not found.");
            return;
        }

        System.out.print("Are you sure you want to delete this room rate? (Y/N)> ");
        String confirm = scanner.nextLine().trim().toUpperCase();
        if (confirm.equals("Y")) {
            try {
                rateSessionBean.deleteRate(rateId);
                System.out.println("Room rate deleted successfully!");
            } catch (IllegalArgumentException e) {
                System.out.println("Unable to delete Room Rate. " + e.getMessage());
            }
        } else {
            System.out.println("Room rate deletion cancelled.");
        }
    }

    private void doViewAllRoomRates() {
        System.out.println("*** View All Room Rates ***");

        List<Rate> rates = rateSessionBean.retrieveAllRates();
        if (rates.isEmpty()) {
            System.out.println("No room rates available.");
            return;
        }

        System.out.printf("%-10s %-15s %-15s %-10s %-15s %-15s %-20s\n", 
                          "Rate ID", "Rate Type", "Rate per Night", "Status", 
                          "Valid From", "Valid To", "Room Type");

        for (Rate rate : rates) {
            System.out.printf("%-10d %-15s %-15.2f %-10s %-15s %-15s %-20s\n",
                              rate.getRateId(),
                              rate.getRateType(),
                              rate.getRatePerNight(),
                              rate.isDisabled() ? "Disabled" : "Active",
                              rate.getValidFrom(),
                              rate.getValidTo(),
                              rate.getRoomType().getName());
        }
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

        // Step 2: Specify number of rooms
        System.out.print("Enter the number of rooms to reserve> ");
        int numberOfRooms = scanner.nextInt();
        scanner.nextLine(); // Consume newline

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

    
    private void doHandleRoomAllocationException() {
        System.out.println("*** Handle Room Allocation Exception ***");

        System.out.print("Enter Reservation ID for the exception> ");
        Long reservationId = scanner.nextLong();
        scanner.nextLine(); // Consume newline

        System.out.print("Enter the exception message> ");
        String message = scanner.nextLine();

        try {
            roomAllocationSessionBean.handleManualRoomAllocationException(reservationId, message);
            System.out.println("Room allocation exception has been logged successfully.");
        } catch (Exception ex) {
            System.out.println("An error occurred while logging the exception: " + ex.getMessage());
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

    
    // Repeat similar structures for each use case as defined in the original prompt...
}
