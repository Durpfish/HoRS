package entity;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

@Entity
public class RoomType implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roomTypeId;

    @Column(length = 64, nullable = false)
    private String name;

    private String description;

    @Column(nullable = false)
    private int capacity;

    @Column(nullable = false)
    private double size;

    private String bedType; // e.g., Queen, King

    private String amenities;

    private boolean disabled;

    
    @OneToOne
    @JoinColumn(name = "nextHigherRoomTypeId")
    private RoomType nextHigherRoomType;
    
//    @Column(nullable = false, unique = true)
//    private Integer roomOrder; // New field for room type hierarchy
    
    @OneToMany(mappedBy = "roomType")
    private List<Room> rooms;
    
    @OneToMany(mappedBy = "roomType")
    private List<Rate> rates;
    
    @OneToMany(mappedBy = "roomType")
    private List<Reservation> reservations;

    public RoomType() {
    }
    
    public RoomType(String name) {
        this.name = name;
    }

    // Getters and Setters
    public Long getRoomTypeId() {
        return roomTypeId;
    }

    public void setRoomTypeId(Long roomTypeId) {
        this.roomTypeId = roomTypeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public double getSize() {
        return size;
    }

    public void setSize(double size) {
        this.size = size;
    }

    public String getBedType() {
        return bedType;
    }

    public void setBedType(String bedType) {
        this.bedType = bedType;
    }

    public String getAmenities() {
        return amenities;
    }

    public void setAmenities(String amenities) {
        this.amenities = amenities;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

//    public Integer getRoomOrder() {
//        return roomOrder;
//    }
//
//    public void setRoomOrder(Integer roomOrder) {
//        this.roomOrder = roomOrder;
//    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (roomTypeId != null ? roomTypeId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof RoomType)) {
            return false;
        }
        RoomType other = (RoomType) object;
        return (this.roomTypeId != null || other.roomTypeId == null) && (this.roomTypeId == null || this.roomTypeId.equals(other.roomTypeId));
    }

    @Override
    public String toString() {
        return name;
    }

    /**
     * @return the nextHigherRoomType
     */
    public RoomType getNextHigherRoomType() {
        return nextHigherRoomType;
    }

    /**
     * @param nextHigherRoomType the nextHigherRoomType to set
     */
    public void setNextHigherRoomType(RoomType nextHigherRoomType) {
        this.nextHigherRoomType = nextHigherRoomType;
    }
}
