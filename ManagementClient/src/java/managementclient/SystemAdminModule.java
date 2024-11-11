/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package managementclient;

import ejb.session.stateless.EmployeeSessionBeanRemote;
import ejb.session.stateless.PartnerSessionBeanRemote;
import ejb.session.stateless.RoomAllocationSessionBeanRemote;
import entity.Employee;
import entity.Partner;
import entity.RoomAllocationExceptionReport;
import java.util.List;
import java.util.Scanner;
import util.enumeration.employeeRole;

/**
 *
 * @author josalyn
 */
public class SystemAdminModule {
 
    private EmployeeSessionBeanRemote employeeSessionBean;
    private PartnerSessionBeanRemote partnerSessionBean;
    private RoomAllocationSessionBeanRemote roomAllocationSessionBean;
    private Scanner scanner = new Scanner(System.in);

    public SystemAdminModule(EmployeeSessionBeanRemote employeeSessionBean, PartnerSessionBeanRemote partnerSessionBean, RoomAllocationSessionBeanRemote roomAllocationSessionBean) {
        this.employeeSessionBean = employeeSessionBean;
        this.partnerSessionBean = partnerSessionBean;
        this.roomAllocationSessionBean = roomAllocationSessionBean;
    }
    
    public void showMenu() {
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
    
    private void doCreateNewEmployee() {
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
            System.out.println("ID: " + employee.getEmployeeId() + ", Name: " + employee.getUsername() + ", Role: " + employee.getRole());
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
}
