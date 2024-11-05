/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package managementclient;

import ejb.session.stateless.RateSessionBeanRemote;
import ejb.session.stateless.RoomTypeSessionBeanRemote;
import entity.Rate;
import entity.RoomType;
import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;
import util.enumeration.rateType;

/**
 *
 * @author josalyn
 */
public class SalesManagerModule {
    
    private RateSessionBeanRemote rateSessionBean;
    private RoomTypeSessionBeanRemote roomTypeSessionBean;
    private Scanner scanner = new Scanner(System.in);

    public SalesManagerModule(RateSessionBeanRemote rateSessionBean, RoomTypeSessionBeanRemote roomTypeSessionBean) {
        this.rateSessionBean = rateSessionBean;
        this.roomTypeSessionBean = roomTypeSessionBean;
    }
    
    public void showMenu() {
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
        
        LocalDate validFrom = null;
        LocalDate validTo = null;

        if (rateTypeChoice == 3 || rateTypeChoice == 4) { // 3 and 4 are peak and promotion
            System.out.print("Enter validity start date (YYYY-MM-DD)> ");
            validFrom = LocalDate.parse(scanner.nextLine());

            System.out.print("Enter validity end date (YYYY-MM-DD)> ");
            validTo = LocalDate.parse(scanner.nextLine());
        }

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

        if (rateTypeChoice == 3 || rateTypeChoice == 4) {
        // Conditionally display "press Enter to keep current" only if current value is not null
            String validFromPrompt = rate.getValidFrom() != null 
                ? "Enter new validity start date (YYYY-MM-DD) (or press Enter to keep current: " + rate.getValidFrom() + ")> " 
                : "Enter validity start date (YYYY-MM-DD)> ";
        
            System.out.print(validFromPrompt);
            String validFromInput = scanner.nextLine();
            if (!validFromInput.isEmpty()) {
                LocalDate validFrom = LocalDate.parse(validFromInput);
                rate.setValidFrom(validFrom);
            }

            String validToPrompt = rate.getValidTo() != null 
                ? "Enter new validity end date (YYYY-MM-DD) (or press Enter to keep current: " + rate.getValidTo() + ")> "
                : "Enter validity end date (YYYY-MM-DD)> ";
        
            System.out.print(validToPrompt);
            String validToInput = scanner.nextLine();
            if (!validToInput.isEmpty()) {
                LocalDate validTo = LocalDate.parse(validToInput);
                rate.setValidTo(validTo);
            }
        } else if (rateTypeChoice == 1 || rateTypeChoice == 2) {
            // Set validity period to null for other rate types (1 or 2)
            rate.setValidFrom(null);
            rate.setValidTo(null);
        } else {
            // User chose to keep the current rate type
            if ((rate.getRateType() == rateType.PEAK || rate.getRateType() == rateType.PROMOTION)) {
                // Prompt to update validity period if the current rate type is 3 or 4 and dates are not null
                System.out.print("Enter new validity start date (YYYY-MM-DD) (or press Enter to keep current: " + rate.getValidFrom() + ")> ");
                String validFromInput = scanner.nextLine();
                if (!validFromInput.isEmpty()) {
                    LocalDate validFrom = LocalDate.parse(validFromInput);
                    rate.setValidFrom(validFrom);
                }

                System.out.print("Enter new validity end date (YYYY-MM-DD) (or press Enter to keep current: " + rate.getValidTo() + ")> ");
                String validToInput = scanner.nextLine();
                if (!validToInput.isEmpty()) {
                    LocalDate validTo = LocalDate.parse(validToInput);
                    rate.setValidTo(validTo);
                }
            } 
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
}
