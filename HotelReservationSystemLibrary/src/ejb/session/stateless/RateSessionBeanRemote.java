package ejb.session.stateless;

import entity.Rate;
import entity.RoomType;
import java.time.LocalDate;
import java.util.List;
import javax.ejb.Remote;
import util.exception.RateDeletionException;

@Remote
public interface RateSessionBeanRemote {

    Long createRate(Rate rate);

    Rate retrieveRateById(Long rateId);

    List<Rate> retrieveAllRates();

    List<Rate> retrieveRatesByType(String rateType);

    void updateRate(Rate rate);

    void deleteRate(Long rateId) throws RateDeletionException;
    
    public Rate retrievePublishedRateForRoomType(Long roomTypeId);
    
    public Rate retrieveApplicableRate(RoomType roomType, LocalDate date);

    public double calculateReservationAmount(RoomType roomType, LocalDate checkInDate, LocalDate checkOutDate);
}
