package clubmanagement.domain.club.vo;

import java.util.List;

/** A town La Poste delivers to, as written on the last line of an address, with its postcodes. */
public record DeliveryTown(String name, List<Postcode> postcodes) {
}
