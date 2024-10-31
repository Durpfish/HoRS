/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
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

/**
 *
 * @author josalyn
 */
@Entity
public class RoomAllocation implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roomAllocationId;
    
    @Column(nullable = false)
    private LocalDate allocationDate;
    
    @ManyToOne(optional = false)
    @JoinColumn(name = "reservationId", nullable = false)
    private Reservation reservation;

    @ManyToOne(optional = false)
    @JoinColumn(name = "roomId", nullable = false)
    private Room room;

    public Long getRoomAllocationId() {
        return roomAllocationId;
    }

    public void setRoomAllocationId(Long roomAllocationId) {
        this.roomAllocationId = roomAllocationId;
    }

    public LocalDate getAllocationDate() {
        return allocationDate;
    }

    public void setAllocationDate(LocalDate allocationDate) {
        this.allocationDate = allocationDate;
    }

    public Reservation getReservation() {
        return reservation;
    }

    public void setReservation(Reservation reservation) {
        this.reservation = reservation;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (roomAllocationId != null ? roomAllocationId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the roomAllocationId fields are not set
        if (!(object instanceof RoomAllocation)) {
            return false;
        }
        RoomAllocation other = (RoomAllocation) object;
        if ((this.roomAllocationId == null && other.roomAllocationId != null) || (this.roomAllocationId != null && !this.roomAllocationId.equals(other.roomAllocationId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.RoomAllocation[ id=" + roomAllocationId + " ]";
    }
    
}
