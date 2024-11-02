package ejb.session.stateless;

import entity.Partner;
import entity.Reservation;
import java.util.List;
import javax.ejb.Local;

@Local
public interface PartnerSessionBeanLocal {

    void registerPartner(Partner partner);

    Partner loginPartner(String username, String password);

    List<Reservation> viewPartnerReservations(Long partnerId);

    Partner findPartnerById(Long partnerId);

    void updatePartnerDetails(Partner partner);

    void deletePartner(Long partnerId);

    public List<Partner> getAllPartners();
}
