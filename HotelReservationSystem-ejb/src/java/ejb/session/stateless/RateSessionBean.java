package ejb.session.stateless;

import entity.Rate;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;
import util.enumeration.rateType;

@Stateless
public class RateSessionBean implements RateSessionBeanRemote, RateSessionBeanLocal {

    @PersistenceContext(unitName = "HotelReservationSystem-ejbPU")
    private EntityManager em;

    public Long createRate(Rate rate) {
        em.persist(rate);
        em.flush();
        return rate.getRateId();
    }

    public Rate retrieveRateById(Long rateId) {
        return em.find(Rate.class, rateId);
    }

    public List<Rate> retrieveAllRates() {
        return em.createQuery("SELECT r FROM Rate r", Rate.class).getResultList();
    }

    public List<Rate> retrieveRatesByType(String rateType) {
        return em.createQuery("SELECT r FROM Rate r WHERE r.rateType = :rateType", Rate.class)
                 .setParameter("rateType", rateType)
                 .getResultList();
    }

    public Rate retrievePublishedRateForRoomType(Long roomTypeId) {
        try {
            return em.createQuery(
                "SELECT r FROM Rate r WHERE r.roomType.roomTypeId = :roomTypeId AND r.rateType = :rateType AND r.disabled = false ORDER BY r.validFrom DESC", Rate.class)
                .setParameter("roomTypeId", roomTypeId)
                .setParameter("rateType", rateType.PUBLISHED)
                .setMaxResults(1)
                .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public void updateRate(Rate rate) {
        em.merge(rate);
    }

    public void deleteRate(Long rateId) {
        Rate rate = retrieveRateById(rateId);
        if (rate != null) {
            long reservationCount = em.createQuery("SELECT COUNT(r) FROM Reservation r WHERE r.rate.rateId = :rateId", Long.class)
                                      .setParameter("rateId", rateId)
                                      .getSingleResult();
            if (reservationCount > 0) {
                throw new IllegalArgumentException("Cannot delete rate as it is associated with existing reservations.");
            }
            em.remove(rate);
        }
    }
}
