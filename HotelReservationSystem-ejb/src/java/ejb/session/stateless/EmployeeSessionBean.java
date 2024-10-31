/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/StatelessEjbClass.java to edit this template
 */
package ejb.session.stateless;

import entity.Employee;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import util.enumeration.employeeRole;

/**
 *
 * @author josalyn
 */
@Stateless
public class EmployeeSessionBean implements EmployeeSessionBeanRemote, EmployeeSessionBeanLocal {

    @PersistenceContext(unitName = "HotelReservationSystem-ejbPU")
    private EntityManager em;
    
    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
    
    public void addEmployee(Employee employee) {
        em.persist(employee); // Save the employee to the database
    }

    public void removeEmployee(Long employeeId) {
        Employee employee = em.find(Employee.class, employeeId);
        if (employee != null) {
            em.remove(employee); // Delete the employee from the database
        }
    }

    public void updateEmployeeDetails(Employee employee) {
        em.merge(employee); // Update employee details
    }

    public List<Employee> getAllEmployees() {
        return em.createQuery("SELECT e FROM Employee e", Employee.class).getResultList();
    }
}
