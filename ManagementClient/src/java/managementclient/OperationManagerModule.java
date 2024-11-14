/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package managementclient;

import ejb.session.stateless.RoomAllocationSessionBeanRemote;
import ejb.session.stateless.RoomSessionBeanRemote;
import ejb.session.stateless.RoomTypeSessionBeanRemote;
import entity.Room;
import entity.RoomAllocationExceptionReport;
import entity.RoomType;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;
import util.enumeration.roomStatus;

/**
 *
 * @author josalyn
 */
public class OperationManagerModule {
    
    private RoomTypeSessionBeanRemote roomTypeSessionBean;
    private RoomSessionBeanRemote roomSessionBean;
    private RoomAllocationSessionBeanRemote roomAllocationSessionBean;
    private Scanner scanner = new Scanner(System.in);

    public OperationManagerModule(RoomTypeSessionBeanRemote roomTypeSessionBean, RoomSessionBeanRemote roomSessionBean, RoomAllocationSessionBeanRemote roomAllocationSessionBean) {
        this.roomTypeSessionBean = roomTypeSessionBean;
        this.roomSessionBean = roomSessionBean;
        this.roomAllocationSessionBean = roomAllocationSessionBean;
    }
    
    public void showMenu() {
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
                    //doManualRoomAllocation(); //for testing
                    break;
                case 11:
                    System.out.println("Logging out...");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        } while (choice != 11);
    }

    private void doManualRoomAllocation() {
        System.out.println("*** Manual Room Allocation ***");

        try {
            System.out.print("Enter date for room allocation (YYYY-MM-DD)> ");
            LocalDate allocationDate = LocalDate.parse(scanner.nextLine());

            roomAllocationSessionBean.allocateRoomsForDate(allocationDate);

            System.out.println("Room allocation completed for date: " + allocationDate);
        } catch (DateTimeParseException e) {
            System.out.println("Invalid date format. Please enter in YYYY-MM-DD format.");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("An error occurred during room allocation: " + e.getMessage());
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

//        System.out.print("Enter hierarchy of this room type> ");
//        int order = scanner.nextInt();
//        scanner.nextLine(); // Consume newline
        
        System.out.print("Enter the name of the next higher room type (or leave blank if none)> ");
        String nextHigherRoomTypeName = scanner.nextLine();

        // Find the next higher room type (if provided)
        RoomType nextHigherRoomType = null;
        if (!nextHigherRoomTypeName.trim().isEmpty()) {
            nextHigherRoomType = roomTypeSessionBean.retrieveRoomTypeByName(nextHigherRoomTypeName);
            if (nextHigherRoomType == null) {
                System.out.println("Room type not found. Please ensure the next higher room type exists before setting it.");
                return;
            }
        }
        
       
        RoomType roomType = new RoomType();
        roomType.setName(name);
        roomType.setDescription(description);
        roomType.setCapacity(capacity);
        roomType.setSize(size);
        roomType.setBedType(bedType);
        roomType.setAmenities(amenities);
         roomType.setNextHigherRoomType(nextHigherRoomType);

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
            System.out.println("Next Higher: " + roomType.getNextHigherRoomType());
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
        System.out.printf("%-10s %-15s %-20s %-15s\n", 
                          "Room ID", "Room Number", "Room Type", "Status");

        for (Room room : rooms) {
            System.out.printf("%-10d %-15s %-20s %-15s\n",
                              room.getRoomId(),
                              room.getRoomNumber(),
                              room.getRoomType().getName(),
                              room.getStatus());
        }
    }
    
    private void doViewRoomAllocationExceptionReport() {
        System.out.println("*** View Room Allocation Exception Report ***");

        List<RoomAllocationExceptionReport> exceptionReports = roomAllocationSessionBean.viewAllRoomAllocationExceptions();

        if (exceptionReports.isEmpty()) {
            System.out.println("No room allocation exceptions found.");
        } else {
            System.out.println("\nRoom Allocation Exception Report:");
            for (RoomAllocationExceptionReport report : exceptionReports) {
                System.out.printf("Report ID: %d\n", report.getReportId());
                System.out.printf("Reservation ID: %d\n", report.getReservation().getReservationId());
                System.out.printf("Exception Date: %s\n", report.getExceptionDate());
                System.out.printf("Message: %s\n\n", report.getMessage());
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
}
