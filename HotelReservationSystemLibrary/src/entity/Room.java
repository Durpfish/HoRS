package entity;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import util.enumeration.roomStatus;

@Entity
public class Room implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roomId;
    
    @Column(nullable = false, unique = true)
    private String roomNumber;
    
    @Column(nullable = false)
    private boolean disabled;

    @ManyToOne(optional = false)
    @JoinColumn(name = "roomTypeId")
    private RoomType roomType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private roomStatus status;

    @OneToMany(mappedBy = "room")
    private List<RoomAllocation> allocations;

    public Room() {
    }


    public Room(RoomType roomType, String roomNumber, roomStatus status) {
        this.roomNumber = roomNumber;
        this.roomType = roomType;
        this.status = status;
    }

    public Long getRoomId() {
        return roomId;
    }

    public void setRoomId(Long roomId) {
        this.roomId = roomId;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    public RoomType getRoomType() {
        return roomType;
    }

    public void setRoomType(RoomType roomType) {
        this.roomType = roomType;
    }

    public roomStatus getStatus() {
        return status;
    }

    public void setStatus(roomStatus status) {
        this.status = status;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (roomId != null ? roomId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the roomId fields are not set
        if (!(object instanceof Room)) {
            return false;
        }
        Room other = (Room) object;
        if ((this.roomId == null && other.roomId != null) || (this.roomId != null && !this.roomId.equals(other.roomId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.Room[ id=" + roomId + " ]";
    }
    
}