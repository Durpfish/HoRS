package managementclient;

import ejb.session.stateless.*;
import javax.ejb.EJB;

public class Main {
    @EJB
    private static EmployeeSessionBeanRemote employeeSessionBean;
    
    @EJB
    private static PartnerSessionBeanRemote partnerSessionBean;
    
    @EJB
    private static RoomTypeSessionBeanRemote roomTypeSessionBean;
    
    @EJB
    private static RoomSessionBeanRemote roomSessionBean;
    
    @EJB
    private static RateSessionBeanRemote rateSessionBean;
    
    @EJB
    private static ReservationSessionBeanRemote reservationSessionBean;
    
    @EJB
    private static RoomAllocationSessionBeanRemote roomAllocationSessionBean;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        MainApp mainApp = new MainApp(employeeSessionBean, partnerSessionBean, roomTypeSessionBean, roomSessionBean, rateSessionBean, reservationSessionBean, roomAllocationSessionBean);
        mainApp.run();
    }
}