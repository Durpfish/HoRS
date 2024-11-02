package ejb.session.stateless;

import entity.Room;
import entity.RoomType;
import java.time.LocalDate;
import java.util.List;
import javax.ejb.Local;

@Local
public interface RoomSessionBeanLocal {

    Long createRoom(Room room);

    Room retrieveRoomById(Long roomId);

    List<Room> retrieveAllRooms();

    List<Room> retrieveRoomsByRoomType(RoomType roomType);

    List<Room> retrieveAvailableRooms();

    void updateRoom(Room room);

    void deleteRoom(Long roomId);

    public List<Room> retrieveAvailableRoomsForDates(LocalDate checkInDate, LocalDate checkOutDate);
}
