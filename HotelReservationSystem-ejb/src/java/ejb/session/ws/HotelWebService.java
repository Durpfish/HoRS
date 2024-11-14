package ejb.session.ws;

import ejb.session.stateless.PartnerSessionBeanLocal;
import ejb.session.stateless.ReservationSessionBeanLocal;
import ejb.session.stateless.RoomSessionBeanLocal;
import ejb.session.stateless.RateSessionBeanLocal;
import ejb.session.stateless.RoomTypeSessionBeanLocal;
import entity.Partner;
import entity.Rate;
import entity.Reservation;
import entity.Room;
import entity.RoomAllocation;
import entity.RoomType;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.ejb.EJB;
import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.ejb.Stateless;
import javax.jws.WebParam;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import util.enumeration.reservationType;

@WebService(serviceName = "HotelWebService", targetNamespace = "http://ws.session.ejb/")
@Stateless()
public class HotelWebService {

    @PersistenceContext(unitName = "HotelReservationSystem-ejbPU")
    private EntityManager em;

    @EJB
    private PartnerSessionBeanLocal partnerSessionBeanLocal;

    @EJB
    private ReservationSessionBeanLocal reservationSessionBeanLocal;

    @EJB
    private RoomSessionBeanLocal roomSessionBeanLocal;
    
    @EJB
    private RateSessionBeanLocal rateSessionBeanLocal;
    
    @EJB
    private RoomTypeSessionBeanLocal roomTypeSessionBeanLocal;
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");


    // Partner-related Web Service Methods
    @WebMethod(operationName = "loginPartner")
    public Partner loginPartner(String username, String password) {
        Partner partner = partnerSessionBeanLocal.partnerLogin(username, password);
        if (partner != null) {
            em.detach(partner);
            partner.setPassword(null); // Hide the password for security
            partner.setReservations(null); // Break the cycle by nullifying the relationship
        }
        return partner;
    }


    // Reservation-related Web Service Methods
    @WebMethod(operationName = "createReservationWithDates")
    public Long createReservationWithDates(
            @WebParam(name = "partnerId") Long partnerId,
            @WebParam(name = "roomTypeId") Long roomTypeId,
            @WebParam(name = "checkInDateStr") String checkInDateStr,
            @WebParam(name = "checkOutDateStr") String checkOutDateStr,
            @WebParam(name = "numberOfGuests") int numberOfGuests) {

        // Convert date strings to LocalDate
        LocalDate checkInDate = LocalDate.parse(checkInDateStr);
        LocalDate checkOutDate = LocalDate.parse(checkOutDateStr);

        // Retrieve the Partner and RoomType entities
        Partner partner = em.find(Partner.class, partnerId);
        RoomType roomType = em.find(RoomType.class, roomTypeId);

        // Validate entities
        if (partner == null || roomType == null) {
            throw new IllegalArgumentException("Partner or RoomType not found");
        }

        // Create and set up the Reservation object
        Reservation reservation = new Reservation();
        reservation.setCheckInDate(checkInDate);
        reservation.setCheckOutDate(checkOutDate);
        reservation.setNumberOfGuests(numberOfGuests);
        reservation.setPartner(partner);
        reservation.setRoomType(roomType);
        reservation.setReservationType(reservationType.PARTNER); // Set desired reservation type

        // Persist the reservation
        em.persist(reservation);
        em.flush(); // Ensures ID is generated

        // Detach the reservation entity if returning more details is needed in the future
        em.detach(reservation);
        reservation.setPartner(null); // Detach partner if unnecessary for client
        reservation.setRoomType(null); // Detach roomType if unnecessary for client

        // Return only the reservation ID
        return reservation.getReservationId();
    }




    @WebMethod(operationName = "retrieveReservationById")
    public Reservation retrieveReservationById(Long reservationId) {
        Reservation reservation = reservationSessionBeanLocal.retrieveReservationById(reservationId);
        if (reservation != null) {
            em.detach(reservation);

            reservation.setCheckInDateFormatted(reservation.getCheckInDate().format(DATE_FORMATTER));
            reservation.setCheckOutDateFormatted(reservation.getCheckOutDate().format(DATE_FORMATTER));
            // Retrieve RoomType by ID if not already loaded, using RoomTypeSessionBean
            RoomType roomType = reservation.getRoomType();
            if (roomType != null) {
                roomType = roomTypeSessionBeanLocal.retrieveRoomTypeById(roomType.getRoomTypeId());
                em.detach(roomType);  // Ensure detached after retrieval
                reservation.setRoomType(roomType);  // Attach it to the reservation
            }

            // Detach Partner if it exists
            Partner partner = reservation.getPartner();
            if (partner != null) {
                em.detach(partner);
            }

            // Detach RoomAllocation and Room if they exist
            RoomAllocation roomAllocation = reservation.getRoomAllocation();
            if (roomAllocation != null) {
                em.detach(roomAllocation);

                Room room = roomAllocation.getRoom();
                if (room != null) {
                    em.detach(room);
                }
            }
        }
        return reservation;
    }


    @WebMethod(operationName = "retrievePartnerReservations")
    public List<Reservation> retrievePartnerReservations(Long partnerId) {
        List<Reservation> reservations = reservationSessionBeanLocal.retrieveReservationsByPartner(partnerId);

        for (Reservation reservation : reservations) {
            em.detach(reservation);
            
            reservation.setCheckInDateFormatted(reservation.getCheckInDate().format(DATE_FORMATTER));
            reservation.setCheckOutDateFormatted(reservation.getCheckOutDate().format(DATE_FORMATTER));

            // Remove Partner details for partner privacy
            reservation.setPartner(null);

            // Detach RoomType internal details if unnecessary for partner
            RoomType roomType = reservation.getRoomType();
            if (roomType != null) {
                em.detach(roomType);
            }

            // Detach RoomAllocation if internal room details aren't necessary
            RoomAllocation roomAllocation = reservation.getRoomAllocation();
            if (roomAllocation != null) {
                em.detach(roomAllocation);
                roomAllocation.setReservation(null); // Prevents circular reference
            }
        }

        return reservations;
    }


    // Room-related Web Service Methods
    @WebMethod(operationName = "retrieveAvailableRoomsForDates")
    public List<Room> retrieveAvailableRoomsForDates(String checkInDateStr, String checkOutDateStr) {
    LocalDate checkInDate = LocalDate.parse(checkInDateStr);
    LocalDate checkOutDate = LocalDate.parse(checkOutDateStr);
        List<Room> rooms = roomSessionBeanLocal.retrieveAvailableRoomsForDates(checkInDate, checkOutDate);
        
        for (Room room : rooms) {
            em.detach(room);
            em.detach(room.getRoomType());
        }
        
        return rooms;
    }

    @WebMethod(operationName = "retrieveAllRooms")
    public List<Room> retrieveAllRooms() {
        List<Room> rooms = roomSessionBeanLocal.retrieveAllRooms();
        rooms.forEach(em::detach);
        return rooms;
    }
    
    @WebMethod(operationName = "calculateRateForStay")
    public double calculateRateForStay(Long roomTypeId, LocalDate checkInDate, LocalDate checkOutDate) {
        RoomType roomType = em.find(RoomType.class, roomTypeId);
        if (roomType == null) {
            throw new IllegalArgumentException("Room type not found.");
        }
        return rateSessionBeanLocal.calculateReservationAmount(roomType, checkInDate, checkOutDate);
    }
    
    @WebMethod(operationName = "retrieveAvailableRoomTypes")
    public List<RoomType> retrieveAvailableRoomTypes(@WebParam(name = "checkInDate") String checkInDateStr,
                                                     @WebParam(name = "checkOutDate") String checkOutDateStr) {
        LocalDate checkInDate = LocalDate.parse(checkInDateStr);
        LocalDate checkOutDate = LocalDate.parse(checkOutDateStr);
        List<RoomType> roomTypes = roomTypeSessionBeanLocal.retrieveAvailableRoomTypes(checkInDate, checkOutDate);
        roomTypes.forEach(em::detach);
        return roomTypes;
    }
    
    @WebMethod(operationName = "retrieveApplicableRate")
    public double retrieveApplicableRate(@WebParam(name = "roomTypeId") Long roomTypeId,
                                         @WebParam(name = "checkInDate") String checkInDateStr) {
        LocalDate checkInDate = LocalDate.parse(checkInDateStr);
        RoomType roomType = em.find(RoomType.class, roomTypeId);
        Rate rate = rateSessionBeanLocal.retrieveApplicableRate(roomType, checkInDate);
        return rate != null ? rate.getRatePerNight() : 0.0;
    }
    
}
