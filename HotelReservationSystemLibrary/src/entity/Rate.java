package entity;

import java.io.Serializable;
import java.time.LocalDate;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import util.enumeration.rateType;

@Entity
public class Rate implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long rateId;
    
    @Column(nullable = false)
    private double ratePerNight;

    @Column(nullable = false)
    private LocalDate validFrom;

    @Column(nullable = false)
    private LocalDate validTo;
    
    @Column(nullable = false)
    private boolean disabled;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private rateType rateType; // Use RateType enum for rate type

    @ManyToOne(optional = false)
    @JoinColumn(name = "roomTypeId")
    private RoomType roomType;

    // Constructors
    public Rate() {
    }

    public Rate(double ratePerNight, LocalDate validFrom, LocalDate validTo, boolean disabled, rateType rateType, RoomType roomType) {
        this.ratePerNight = ratePerNight;
        this.validFrom = validFrom;
        this.validTo = validTo;
        this.disabled = disabled;
        this.rateType = rateType;
        this.roomType = roomType;
    }

    // Getters and Setters
    public Long getRateId() {
        return rateId;
    }

    public void setRateId(Long rateId) {
        this.rateId = rateId;
    }

    public double getRatePerNight() {
        return ratePerNight;
    }

    public void setRatePerNight(double ratePerNight) {
        this.ratePerNight = ratePerNight;
    }

    public LocalDate getValidFrom() {
        return validFrom;
    }

    public void setValidFrom(LocalDate validFrom) {
        this.validFrom = validFrom;
    }

    public LocalDate getValidTo() {
        return validTo;
    }

    public void setValidTo(LocalDate validTo) {
        this.validTo = validTo;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    public rateType getRateType() {
        return rateType;
    }

    public void setRateType(rateType rateType) {
        this.rateType = rateType;
    }

    public RoomType getRoomType() {
        return roomType;
    }

    public void setRoomType(RoomType roomType) {
        this.roomType = roomType;
    }

    @Override
    public int hashCode() {
        return (rateId != null ? rateId.hashCode() : 0);
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Rate)) {
            return false;
        }
        Rate other = (Rate) object;
        return (this.rateId != null || other.rateId == null) && (this.rateId == null || this.rateId.equals(other.rateId));
    }

    @Override
    public String toString() {
        return "entity.Rate[ id=" + rateId + " ]";
    }
}
