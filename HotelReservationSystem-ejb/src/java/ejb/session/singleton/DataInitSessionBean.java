package ejb.session.singleton;

import ejb.session.stateless.EmployeeSessionBeanLocal;
import ejb.session.stateless.RoomSessionBeanLocal;
import ejb.session.stateless.RoomTypeSessionBeanLocal;
import ejb.session.stateless.RateSessionBeanLocal;
import entity.Employee;
import entity.Room;
import entity.RoomType;
import entity.Rate;
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
            employeeSessionBean.addEmployee(new Employee("Alice", "Smith", "alice", "password123", employeeRole.SYSTEM_ADMINISTRATOR));
            employeeSessionBean.addEmployee(new Employee("Bob", "Johnson", "bob", "password123", employeeRole.OPERATION_MANAGER));
            employeeSessionBean.addEmployee(new Employee("Steve", "Jones", "steve", "password123", employeeRole.SALES_MANAGER));
            employeeSessionBean.addEmployee(new Employee("Laura", "Miller", "laura", "password123", employeeRole.GUEST_RELATION_OFFICER));
            }
            
        }
    }
