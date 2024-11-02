/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/SessionRemote.java to edit this template
 */
package ejb.session.stateless;

import entity.Employee;
import java.util.List;
import javax.ejb.Remote;

/**
 *
 * @author josalyn
 */
@Remote
public interface EmployeeSessionBeanRemote {
    public void addEmployee(Employee employee);

    public void removeEmployee(Long employeeId);

    public void updateEmployeeDetails(Employee employee);

    public List<Employee> getAllEmployees();

    public Employee findEmployeeById(Long employeeId);

    public Employee findEmployeeByUsername(String username); 
    
    public Employee loginEmployee(String username, String password);
}
