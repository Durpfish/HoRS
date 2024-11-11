package ejb.session.stateless;

import entity.RoomType;
import java.time.LocalDate;
import java.util.List;
import javax.ejb.Remote;

@Remote
public interface RoomTypeSessionBeanRemote {

    Long createRoomType(RoomType roomType);

    RoomType retrieveRoomTypeById(Long roomTypeId);

    List<RoomType> retrieveAllRoomTypes();

    public List<RoomType> retrieveAvailableRoomTypes(LocalDate checkInDate, LocalDate checkOutDate);

    void updateRoomType(RoomType roomType);

    void deleteRoomType(Long roomTypeId);
    
    public RoomType retrieveRoomTypeByName(String name);
}
