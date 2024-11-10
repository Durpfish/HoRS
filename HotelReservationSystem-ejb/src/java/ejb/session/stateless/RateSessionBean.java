package ejb.session.stateless;

import entity.Rate;
import entity.RoomType;
import java.time.LocalDate;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import util.enumeration.rateType;

@Stateless
public class RateSessionBean implements RateSessionBeanRemote, RateSessionBeanLocal {

    @PersistenceContext(unitName = "HotelReservationSystem-ejbPU")
    private EntityManager em;

    @Override
    public Long createRate(Rate rate) {
        em.persist(rate);
        em.flush();
        return rate.getRateId();
    }

    @Override
    public Rate retrieveRateById(Long rateId) {
        return em.find(Rate.class, rateId);
    }

    @Override
    public List<Rate> retrieveAllRates() {
        return em.createQuery("SELECT r FROM Rate r", Rate.class).getResultList();
    }

    public List<Rate> retrieveRatesByType(String rateType) {
        return em.createQuery("SELECT r FROM Rate r WHERE r.rateType = :rateType", Rate.class)
                 .setParameter("rateType", rateType)
                 .getResultList();
    }

    @Override
    public Rate retrievePublishedRateForRoomType(Long roomTypeId) {
        try {
            return em.createQuery(
                "SELECT r FROM Rate r WHERE r.roomType.roomTypeId = :roomTypeId " +
                "AND r.rateType = :rateType AND r.disabled = false " +
                "ORDER BY CASE WHEN r.validFrom IS NULL THEN 1 ELSE 0 END, r.validFrom DESC", Rate.class)
                .setParameter("roomTypeId", roomTypeId)
                .setParameter("rateType", rateType.PUBLISHED)
                .setMaxResults(1)
                .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public void updateRate(Rate rate) {
        em.merge(rate);
    }

    @Override
    public void deleteRate(Long rateId) {
        Rate rate = retrieveRateById(rateId);
        if (rate != null) {
            RoomType roomType = rate.getRoomType();
            long reservationCount = em.createQuery("SELECT COUNT(r) FROM Reservation r WHERE r.roomType = :roomType AND r.rate = :rate", Long.class)
                                                .setParameter("roomType", roomType)
                                                .setParameter("rate", rate)
                                                .getSingleResult();
            if (reservationCount > 0) {
                throw new IllegalArgumentException("Cannot delete rate as it is associated with existing reservations.");
            }
            em.remove(rate);
        }
    }

    @Override
    public Rate retrieveApplicableRate(RoomType roomType, LocalDate date) {
        try {
            TypedQuery<Rate> query = em.createQuery(
                "SELECT r FROM Rate r WHERE r.roomType = :roomType AND r.disabled = false " +
                "AND (r.validFrom IS NULL OR r.validFrom <= :date) " +
                "AND (r.validUntil IS NULL OR r.validUntil >= :date) " +
                "ORDER BY CASE WHEN r.rateType = :promotion THEN 0 " +
                "WHEN r.rateType = :peak THEN 1 " +
                "WHEN r.rateType = :normal THEN 2 " +
                "ELSE 3 END", Rate.class);
            query.setParameter("roomType", roomType);
            query.setParameter("date", date);
            query.setParameter("promotion", rateType.PROMOTION);
            query.setParameter("peak", rateType.PEAK);
            query.setParameter("normal", rateType.NORMAL);
            return query.setMaxResults(1).getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public double calculateReservationAmount(RoomType roomType, LocalDate checkInDate, LocalDate checkOutDate) {
        double totalAmount = 0.0;
        LocalDate currentDate = checkInDate;

        while (!currentDate.isAfter(checkOutDate)) {
            Rate applicableRate = retrieveApplicableRate(roomType, currentDate);
            if (applicableRate != null) {
                totalAmount += applicableRate.getRatePerNight();
            }
            currentDate = currentDate.plusDays(1);
        }

        return totalAmount;
    }
}

