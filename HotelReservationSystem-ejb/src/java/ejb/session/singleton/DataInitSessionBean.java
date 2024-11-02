/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB31/SingletonEjbClass.java to edit this template
 */
package ejb.session.singleton;

import ejb.session.stateless.EmployeeSessionBeanLocal;
import entity.Employee;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.LocalBean;
import javax.ejb.Startup;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import util.enumeration.employeeRole;

/**
 *
 * @author josalyn
 */
@Singleton
@LocalBean
@Startup 

public class DataInitSessionBean {

    @EJB
    private EmployeeSessionBeanLocal employeeSessionBean;

    @PersistenceContext(unitName = "HotelReservationSystem-ejbPU")
    private EntityManager em;
    
    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
    
    @PostConstruct
    public void postConstruct() {
        if (em.find(Employee.class, 1l) == null) {
            employeeSessionBean.addEmployee(new Employee("Alice", "Smith", "alice", "password123", employeeRole.SYSTEM_ADMIN));
            employeeSessionBean.addEmployee(new Employee("Bob", "Johnson", "bob", "password123", employeeRole.OPERATION_MANAGER));
            employeeSessionBean.addEmployee(new Employee("Steve", "Jones", "steve", "password123", employeeRole.SALES_MANAGER));
            employeeSessionBean.addEmployee(new Employee("Laura", "Miller", "laura", "password123", employeeRole.GUEST_RELATION_OFFICER));
        }
        
    }
}
