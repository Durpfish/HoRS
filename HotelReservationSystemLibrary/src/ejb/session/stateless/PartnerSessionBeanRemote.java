package ejb.session.stateless;

import entity.Partner;
import entity.Reservation;
import java.util.List;
import javax.ejb.Remote;

@Remote
public interface PartnerSessionBeanRemote {

    void registerPartner(Partner partner);

    List<Reservation> viewPartnerReservations(Long partnerId);

    Partner findPartnerById(Long partnerId);

    void updatePartnerDetails(Partner partner);

    void deletePartner(Long partnerId);

    public List<Partner> getAllPartners();

    public Long createPartner(Partner partner);

    public Partner partnerLogin(String username, String password);

    public Partner retrievePartnerById(Long partnerId);

    public Partner retrievePartnerByUsername(String username);

    public void updatePartner(Partner partner);
}
