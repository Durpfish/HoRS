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
import util.enumeration.rateType;
import util.enumeration.roomStatus;

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
    
    @EJB
    private RoomTypeSessionBeanLocal roomTypeSessionBean;
    
    @EJB
    private RateSessionBeanLocal rateSessionBean;
    
    @EJB
    private RoomSessionBeanLocal roomSessionBean;
    
    @PersistenceContext(unitName = "HotelReservationSystem-ejbPU")
    private EntityManager em;
    
    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
    
    @PostConstruct
    public void postConstruct() {
        if (em.find(Employee.class, 1l) == null) {
            employeeSessionBean.addEmployee(new Employee("sysadmin", "password", employeeRole.SYSTEM_ADMINISTRATOR));
            employeeSessionBean.addEmployee(new Employee("opmanager", "password", employeeRole.OPERATION_MANAGER));
            employeeSessionBean.addEmployee(new Employee("salesmanager", "password", employeeRole.SALES_MANAGER));
            employeeSessionBean.addEmployee(new Employee("guestrelo", "password", employeeRole.GUEST_RELATION_OFFICER));
        }
        
        if (em.find(RoomType.class, 1l) == null) {
            RoomType deluxeRoom = new RoomType("Deluxe Room");
            RoomType premierRoom = new RoomType("Premier Room");
            RoomType familyRoom = new RoomType("Family Room");
            RoomType juniorSuite = new RoomType("Junior Suite");
            RoomType grandSuite = new RoomType("Grand Suite");

            // Persist each RoomType using createRoomType method to assign them a primary key
            deluxeRoom.setRoomTypeId(roomTypeSessionBean.createRoomType(deluxeRoom));
            premierRoom.setRoomTypeId(roomTypeSessionBean.createRoomType(premierRoom));
            familyRoom.setRoomTypeId(roomTypeSessionBean.createRoomType(familyRoom));
            juniorSuite.setRoomTypeId(roomTypeSessionBean.createRoomType(juniorSuite));
            grandSuite.setRoomTypeId(roomTypeSessionBean.createRoomType(grandSuite));

            // Step 2: Set nextHigherRoomType relationships
            deluxeRoom.setNextHigherRoomType(premierRoom);
            premierRoom.setNextHigherRoomType(familyRoom);
            familyRoom.setNextHigherRoomType(juniorSuite);
            juniorSuite.setNextHigherRoomType(grandSuite);
            // grandSuite is the highest room type, so no nextHigherRoomType

            // Use the updateRoomType or createRoomType method if it handles updates as well
            roomTypeSessionBean.updateRoomType(deluxeRoom);
            roomTypeSessionBean.updateRoomType(premierRoom);
            roomTypeSessionBean.updateRoomType(familyRoom);
            roomTypeSessionBean.updateRoomType(juniorSuite);
            roomTypeSessionBean.updateRoomType(grandSuite);

        }
        
        if (em.find(Rate.class, 1l) == null) {
            RoomType deluxeRoom = roomTypeSessionBean.retrieveRoomTypeByName("Deluxe Room");
            RoomType premierRoom = roomTypeSessionBean.retrieveRoomTypeByName("Premier Room");
            RoomType familyRoom = roomTypeSessionBean.retrieveRoomTypeByName("Family Room");
            RoomType juniorSuite = roomTypeSessionBean.retrieveRoomTypeByName("Junior Suite");
            RoomType grandSuite = roomTypeSessionBean.retrieveRoomTypeByName("Grand Suite");

            // Initialize rates for each room type using createRate method
            rateSessionBean.createRate(new Rate("Deluxe Room Published", deluxeRoom, rateType.PUBLISHED, 100));
            rateSessionBean.createRate(new Rate("Deluxe Room Normal", deluxeRoom, rateType.NORMAL, 50));
            rateSessionBean.createRate(new Rate("Premier Room Published", premierRoom, rateType.PUBLISHED, 200));
            rateSessionBean.createRate(new Rate("Premier Room Normal", premierRoom, rateType.NORMAL, 100));
            rateSessionBean.createRate(new Rate("Family Room Published", familyRoom, rateType.PUBLISHED, 300));
            rateSessionBean.createRate(new Rate("Family Room Normal", familyRoom, rateType.NORMAL, 150));
            rateSessionBean.createRate(new Rate("Junior Suite Published", juniorSuite, rateType.PUBLISHED, 400));
            rateSessionBean.createRate(new Rate("Junior Suite Normal", juniorSuite, rateType.NORMAL, 200));
            rateSessionBean.createRate(new Rate("Grand Suite Published", grandSuite, rateType.PUBLISHED, 500));
            rateSessionBean.createRate(new Rate("Grand Suite Normal", grandSuite, rateType.NORMAL, 250));

        }
        
        if (em.find(Room.class, 1L) == null) {
            RoomType deluxeRoom = roomTypeSessionBean.retrieveRoomTypeByName("Deluxe Room");
            RoomType premierRoom = roomTypeSessionBean.retrieveRoomTypeByName("Premier Room");
            RoomType familyRoom = roomTypeSessionBean.retrieveRoomTypeByName("Family Room");
            RoomType juniorSuite = roomTypeSessionBean.retrieveRoomTypeByName("Junior Suite");
            RoomType grandSuite = roomTypeSessionBean.retrieveRoomTypeByName("Grand Suite");

            // Initialize Deluxe Rooms
            roomSessionBean.createRoom(new Room(deluxeRoom, "0101", roomStatus.AVAILABLE));
            roomSessionBean.createRoom(new Room(deluxeRoom, "0201", roomStatus.AVAILABLE));
            roomSessionBean.createRoom(new Room(deluxeRoom, "0301", roomStatus.AVAILABLE));
            roomSessionBean.createRoom(new Room(deluxeRoom, "0401", roomStatus.AVAILABLE));
            roomSessionBean.createRoom(new Room(deluxeRoom, "0501", roomStatus.AVAILABLE));

            // Initialize Premier Rooms
            roomSessionBean.createRoom(new Room(premierRoom, "0102", roomStatus.AVAILABLE));
            roomSessionBean.createRoom(new Room(premierRoom, "0202", roomStatus.AVAILABLE));
            roomSessionBean.createRoom(new Room(premierRoom, "0302", roomStatus.AVAILABLE));
            roomSessionBean.createRoom(new Room(premierRoom, "0402", roomStatus.AVAILABLE));
            roomSessionBean.createRoom(new Room(premierRoom, "0502", roomStatus.AVAILABLE));

            // Initialize Family Rooms
            roomSessionBean.createRoom(new Room(familyRoom, "0103", roomStatus.AVAILABLE));
            roomSessionBean.createRoom(new Room(familyRoom, "0203", roomStatus.AVAILABLE));
            roomSessionBean.createRoom(new Room(familyRoom, "0303", roomStatus.AVAILABLE));
            roomSessionBean.createRoom(new Room(familyRoom, "0403", roomStatus.AVAILABLE));
            roomSessionBean.createRoom(new Room(familyRoom, "0503", roomStatus.AVAILABLE));

            // Initialize Junior Suites
            roomSessionBean.createRoom(new Room(juniorSuite, "0104", roomStatus.AVAILABLE));
            roomSessionBean.createRoom(new Room(juniorSuite, "0204", roomStatus.AVAILABLE));
            roomSessionBean.createRoom(new Room(juniorSuite, "0304", roomStatus.AVAILABLE));
            roomSessionBean.createRoom(new Room(juniorSuite, "0404", roomStatus.AVAILABLE));
            roomSessionBean.createRoom(new Room(juniorSuite, "0504", roomStatus.AVAILABLE));

            // Initialize Grand Suites
            roomSessionBean.createRoom(new Room(grandSuite, "0105", roomStatus.AVAILABLE));
            roomSessionBean.createRoom(new Room(grandSuite, "0205", roomStatus.AVAILABLE));
            roomSessionBean.createRoom(new Room(grandSuite, "0305", roomStatus.AVAILABLE));
            roomSessionBean.createRoom(new Room(grandSuite, "0405", roomStatus.AVAILABLE));
            roomSessionBean.createRoom(new Room(grandSuite, "0505", roomStatus.AVAILABLE));
            
        }    
    }  
}
