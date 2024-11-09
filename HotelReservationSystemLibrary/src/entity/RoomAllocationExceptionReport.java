package entity;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.time.LocalDate;
import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import util.enumeration.ExceptionType;

@Entity
public class RoomAllocationExceptionReport implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reportId;

    @ManyToOne(optional = false)
    @JoinColumn(nullable = false)
    private Reservation reservation;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ExceptionType exceptionType;


    private String message;
    private LocalDate exceptionDate;

    // Constructors
    public RoomAllocationExceptionReport() {
    }

    public RoomAllocationExceptionReport(Reservation reservation, String message, LocalDate exceptionDate) {
        this.reservation = reservation;
        this.message = message;
        this.exceptionDate = exceptionDate;
    }

    // Getters and Setters
    public Long getReportId() {
        return reportId;
    }

    public void setReportId(Long reportId) {
        this.reportId = reportId;
    }

    public Reservation getReservation() {
        return reservation;
    }

    public void setReservation(Reservation reservation) {
        this.reservation = reservation;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDate getExceptionDate() {
        return exceptionDate;
    }

    public void setExceptionDate(LocalDate exceptionDate) {
        this.exceptionDate = exceptionDate;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (reportId != null ? reportId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof RoomAllocationExceptionReport)) {
            return false;
        }
        RoomAllocationExceptionReport other = (RoomAllocationExceptionReport) object;
        return (this.reportId != null || other.reportId == null) && (this.reportId == null || this.reportId.equals(other.reportId));
    }

    @Override
    public String toString() {
        return "RoomAllocationExceptionReport[ id=" + reportId +
               ", reservationId=" + (reservation != null ? reservation.getReservationId() : "null") +
               ", message=" + message +
               ", exceptionDate=" + exceptionDate + " ]";
    }

}
