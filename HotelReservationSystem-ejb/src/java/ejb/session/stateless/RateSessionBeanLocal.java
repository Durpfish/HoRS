package ejb.session.stateless;

import entity.Rate;
import java.util.List;
import javax.ejb.Local;

@Local
public interface RateSessionBeanLocal {

    Long createRate(Rate rate);

    Rate retrieveRateById(Long rateId);

    List<Rate> retrieveAllRates();

    List<Rate> retrieveRatesByType(String rateType);

    void updateRate(Rate rate);

    void deleteRate(Long rateId);

    public Rate retrievePublishedRateForRoomType(Long roomTypeId);
}
