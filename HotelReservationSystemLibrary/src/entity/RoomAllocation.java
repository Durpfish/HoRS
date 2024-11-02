package entity;

import java.io.Serializable;
import java.time.LocalDate;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.ManyToOne;

@Entity
public class RoomAllocation implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long allocationId;

    @Column(nullable = false)
    private LocalDate allocationDate;

    @ManyToOne(optional = false)
    @JoinColumn(name = "roomId", nullable = false)
    private Room room;

    @OneToOne(optional = false)
    @JoinColumn(name = "reservationId", unique = true)
    private Reservation reservation;

    private String allocationExceptionReport; // Optional field to store allocation exceptions

    // Constructors
    public RoomAllocation() {
    }

    public RoomAllocation(LocalDate allocationDate, Room room, Reservation reservation, String allocationExceptionReport) {
        this.allocationDate = allocationDate;
        this.room = room;
        this.reservation = reservation;
        this.allocationExceptionReport = allocationExceptionReport;
    }

    // Getters and Setters
    public Long getAllocationId() {
        return allocationId;
    }

    public void setAllocationId(Long allocationId) {
        this.allocationId = allocationId;
    }

    public LocalDate getAllocationDate() {
        return allocationDate;
    }

    public void setAllocationDate(LocalDate allocationDate) {
        this.allocationDate = allocationDate;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public Reservation getReservation() {
        return reservation;
    }

    public void setReservation(Reservation reservation) {
        this.reservation = reservation;
    }

    public String getAllocationExceptionReport() {
        return allocationExceptionReport;
    }

    public void setAllocationExceptionReport(String allocationExceptionReport) {
        this.allocationExceptionReport = allocationExceptionReport;
    }

    @Override
    public int hashCode() {
        return (allocationId != null ? allocationId.hashCode() : 0);
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof RoomAllocation)) {
            return false;
        }
        RoomAllocation other = (RoomAllocation) object;
        return (this.allocationId != null || other.allocationId == null) && (this.allocationId == null || this.allocationId.equals(other.allocationId));
    }

    @Override
    public String toString() {
        return "entity.RoomAllocation[ id=" + allocationId + " ]";
    }
}
