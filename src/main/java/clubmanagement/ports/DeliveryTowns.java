package clubmanagement.ports;

import clubmanagement.domain.club.vo.Postcode;

import java.util.List;

/** Port: the towns La Poste delivers to, written as it expects them on the last line of an address. */
public interface DeliveryTowns {
    List<String> servedBy(Postcode postcode);

    List<Postcode> postcodesOf(String town);
}
