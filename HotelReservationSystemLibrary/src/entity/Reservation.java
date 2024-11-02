package entity;

import java.io.Serializable;
import java.time.LocalDate;
import javax.persistence.*;
import util.enumeration.reservationType;

@Entity
public class Reservation implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reservationId;

    @Column(nullable = false)
    private LocalDate checkInDate;

    @Column(nullable = false)
    private LocalDate checkOutDate;

    @Column(nullable = false)
    private LocalDate reservationDate;

    @Column(nullable = false)
    private int numberOfGuests;

    @ManyToOne(optional = false)
    @JoinColumn(name = "guestId")
    private Guest guest; // Many-to-one relationship with Guest

    @ManyToOne(optional = false)
    @JoinColumn(name = "roomTypeId")
    private RoomType roomType; // Many-to-one relationship with RoomType

    @ManyToOne(optional = true)
    @JoinColumn(name = "partnerId")
    private Partner partner; // Optional relationship with Partner

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private reservationType reservationType; // Enum for ONLINE, WALK-IN, PARTNER

    @OneToOne(mappedBy = "reservation")
    private RoomAllocation roomAllocation; // One-to-one relationship with RoomAllocation

    @ManyToOne
    @JoinColumn(name = "allocatedRoomId")
    private Room allocatedRoom; // New field to store the allocated room

    // Constructors
    public Reservation() {
    }

    public Reservation(LocalDate checkInDate, LocalDate checkOutDate, LocalDate reservationDate, int numberOfGuests, Guest guest, RoomType roomType, Partner partner, reservationType reservationType) {
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.reservationDate = reservationDate;
        this.numberOfGuests = numberOfGuests;
        this.guest = guest;
        this.roomType = roomType;
        this.partner = partner;
        this.reservationType = reservationType;
    }

    // Getters and Setters
    public Long getReservationId() {
        return reservationId;
    }

    public void setReservationId(Long reservationId) {
        this.reservationId = reservationId;
    }

    public LocalDate getCheckInDate() {
        return checkInDate;
    }

    public void setCheckInDate(LocalDate checkInDate) {
        this.checkInDate = checkInDate;
    }

    public LocalDate getCheckOutDate() {
        return checkOutDate;
    }

    public void setCheckOutDate(LocalDate checkOutDate) {
        this.checkOutDate = checkOutDate;
    }

    public LocalDate getReservationDate() {
        return reservationDate;
    }

    public void setReservationDate(LocalDate reservationDate) {
        this.reservationDate = reservationDate;
    }

    public int getNumberOfGuests() {
        return numberOfGuests;
    }

    public void setNumberOfGuests(int numberOfGuests) {
        this.numberOfGuests = numberOfGuests;
    }

    public Guest getGuest() {
        return guest;
    }

    public void setGuest(Guest guest) {
        this.guest = guest;
    }

    public RoomType getRoomType() {
        return roomType;
    }

    public void setRoomType(RoomType roomType) {
        this.roomType = roomType;
    }

    public Partner getPartner() {
        return partner;
    }

    public void setPartner(Partner partner) {
        this.partner = partner;
    }

    public reservationType getReservationType() {
        return reservationType;
    }

    public void setReservationType(reservationType reservationType) {
        this.reservationType = reservationType;
    }

    public RoomAllocation getRoomAllocation() {
        return roomAllocation;
    }

    public void setRoomAllocation(RoomAllocation roomAllocation) {
        this.roomAllocation = roomAllocation;
    }

    public Room getAllocatedRoom() {
        return allocatedRoom;
    }

    public void setAllocatedRoom(Room allocatedRoom) {
        this.allocatedRoom = allocatedRoom;
    }

    @Override
    public int hashCode() {
        return (reservationId != null ? reservationId.hashCode() : 0);
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Reservation)) {
            return false;
        }
        Reservation other = (Reservation) object;
        return (this.reservationId != null || other.reservationId == null) && (this.reservationId == null || this.reservationId.equals(other.reservationId));
    }

    @Override
    public String toString() {
        return "entity.Reservation[ id=" + reservationId + " ]";
    }
}
