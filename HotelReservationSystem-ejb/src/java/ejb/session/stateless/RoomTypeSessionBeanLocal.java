package ejb.session.stateless;

import entity.RoomType;
import java.time.LocalDate;
import java.util.List;
import javax.ejb.Local;

@Local
public interface RoomTypeSessionBeanLocal {

    Long createRoomType(RoomType roomType);

    RoomType retrieveRoomTypeById(Long roomTypeId);

    List<RoomType> retrieveAllRoomTypes();

    void updateRoomType(RoomType roomType);

    void deleteRoomType(Long roomTypeId);

    public List<RoomType> retrieveAvailableRoomTypes(LocalDate checkInDate, LocalDate checkOutDate);

    public RoomType retrieveRoomTypeByName(String name);
}
