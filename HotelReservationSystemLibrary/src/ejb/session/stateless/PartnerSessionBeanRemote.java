package ejb.session.stateless;

import entity.Partner;
import entity.Reservation;
import java.util.List;
import javax.ejb.Remote;

@Remote
public interface PartnerSessionBeanRemote {

    void registerPartner(Partner partner);

    Partner loginPartner(String username, String password);

    List<Reservation> viewPartnerReservations(Long partnerId);

    Partner findPartnerById(Long partnerId);

    void updatePartnerDetails(Partner partner);

    void deletePartner(Long partnerId);
    
    public List<Partner> getAllPartners();
}
