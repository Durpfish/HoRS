package reservationclient;

import ejb.session.stateless.*;
import javax.ejb.EJB;

public class Main {
    @EJB
    private static GuestSessionBeanRemote guestSessionBean;

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

    public static void main(String[] args) {
        MainApp mainApp = new MainApp(guestSessionBean, roomTypeSessionBean, roomSessionBean, rateSessionBean, reservationSessionBean, roomAllocationSessionBean);
        mainApp.run();
    }
}
