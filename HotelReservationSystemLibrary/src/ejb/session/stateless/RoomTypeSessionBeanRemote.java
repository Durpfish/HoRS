package ejb.session.stateless;

import entity.RoomType;
import java.util.List;
import javax.ejb.Remote;

@Remote
public interface RoomTypeSessionBeanRemote {

    Long createRoomType(RoomType roomType);

    RoomType retrieveRoomTypeById(Long roomTypeId);

    List<RoomType> retrieveAllRoomTypes();

    List<RoomType> retrieveAvailableRoomTypes();

    void updateRoomType(RoomType roomType);

    void deleteRoomType(Long roomTypeId);
}
