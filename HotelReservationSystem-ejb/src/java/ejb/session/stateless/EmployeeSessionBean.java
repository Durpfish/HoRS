package ejb.session.stateless;

import entity.Employee;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

@Stateless
public class EmployeeSessionBean implements EmployeeSessionBeanRemote, EmployeeSessionBeanLocal {

    @PersistenceContext(unitName = "HotelReservationSystem-ejbPU")
    private EntityManager em;
    
    @Override
    public void addEmployee(Employee employee) {
        if (isUsernameUnique(employee.getUsername())) {
            em.persist(employee);
        } else {
            throw new IllegalArgumentException("Username is already in use.");
        }
    }

    @Override
    public void removeEmployee(Long employeeId) {
        Employee employee = findEmployeeById(employeeId);
        if (employee != null) {
            em.remove(employee);
        }
    }

    @Override
    public void updateEmployeeDetails(Employee employee) {
        if (isUsernameUnique(employee.getUsername()) || isUpdatingOwnAccount(employee)) {
            em.merge(employee);
        } else {
            throw new IllegalArgumentException("Username is already in use by another account.");
        }
    }

    @Override
    public List<Employee> getAllEmployees() {
        return em.createQuery("SELECT e FROM Employee e", Employee.class).getResultList();
    }

    @Override
    public Employee findEmployeeById(Long employeeId) {
        return em.find(Employee.class, employeeId);
    }

    @Override
    public Employee findEmployeeByUsername(String username) {
        try {
            Query query = em.createQuery("SELECT e FROM Employee e WHERE e.username = :username", Employee.class);
            query.setParameter("username", username);
            return (Employee) query.getSingleResult();
        } catch (NoResultException e) {
            return null; // Return null if no employee is found
        }
    }

    public Employee loginEmployee(String username, String password) {
        Employee employee = findEmployeeByUsername(username);
        if (employee != null && employee.getPassword().equals(password)) {
            return employee; // Successful login
        } else {
            return null; // Invalid credentials
        }
    }

    private boolean isUsernameUnique(String username) {
        Query query = em.createQuery("SELECT COUNT(e) FROM Employee e WHERE e.username = :username");
        query.setParameter("username", username);
        Long count = (Long) query.getSingleResult();
        return count == 0;
    }
    
    private boolean isUpdatingOwnAccount(Employee employee) {
        Employee existingEmployee = findEmployeeById(employee.getEmployeeId());
        return existingEmployee != null && existingEmployee.getUsername().equals(employee.getUsername());
    }
}
