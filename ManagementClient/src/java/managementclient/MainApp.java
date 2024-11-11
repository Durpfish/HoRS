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
    
    private SystemAdminModule systemAdminModule;
    private OperationManagerModule operationManagerModule;
    private SalesManagerModule salesManagerModule;
    private GuestRelationOfficerModule guestRelationOfficerModule;

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
        
        this.systemAdminModule = new SystemAdminModule(employeeSessionBean, partnerSessionBean, roomAllocationSessionBean);
        this.operationManagerModule = new OperationManagerModule(roomTypeSessionBean, roomSessionBean, roomAllocationSessionBean);
        this.salesManagerModule = new SalesManagerModule(rateSessionBean, roomTypeSessionBean);
        this.guestRelationOfficerModule = new GuestRelationOfficerModule(roomSessionBean, rateSessionBean, reservationSessionBean, roomAllocationSessionBean);
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
            case SYSTEM_ADMINISTRATOR:
                systemAdminModule.showMenu();
                break;
            case OPERATION_MANAGER:
                operationManagerModule.showMenu();
                break;
            case SALES_MANAGER:
                salesManagerModule.showMenu();
                break;
            case GUEST_RELATION_OFFICER:
                guestRelationOfficerModule.showMenu();
                break;
            default:
                System.out.println("Invalid role");
                break;
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
