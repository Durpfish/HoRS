package entity;

import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;

@Entity
@PrimaryKeyJoinColumn(name = "guestId") // Reuse guestId as primary key for Customer
public class Customer extends Guest {

    public Customer() {
    }

    public Customer(String firstName, String lastName, String email, String phoneNumber, String passportNumber, String username, String password) {
        super(firstName, lastName, email, phoneNumber, passportNumber, username, password);
    }

    @Override
    public int hashCode() {
        return (getGuestId() != null ? getGuestId().hashCode() : 0);
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Customer)) {
            return false;
        }
        Customer other = (Customer) object;
        return (this.getGuestId() != null || other.getGuestId() == null) && (this.getGuestId() == null || this.getGuestId().equals(other.getGuestId()));
    }

    @Override
    public String toString() {
        return "entity.Customer[ id=" + getGuestId() + " ]";
    }

}
