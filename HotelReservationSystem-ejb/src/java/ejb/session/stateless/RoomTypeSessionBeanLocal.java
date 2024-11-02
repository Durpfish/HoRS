package ejb.session.stateless;

import entity.RoomType;
import java.util.List;
import javax.ejb.Local;

@Local
public interface RoomTypeSessionBeanLocal {

    Long createRoomType(RoomType roomType);

    RoomType retrieveRoomTypeById(Long roomTypeId);

    List<RoomType> retrieveAllRoomTypes();

    List<RoomType> retrieveAvailableRoomTypes();

    void updateRoomType(RoomType roomType);

    void deleteRoomType(Long roomTypeId);
}
