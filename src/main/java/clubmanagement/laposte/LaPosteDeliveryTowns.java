package clubmanagement.laposte;

import clubmanagement.domain.club.vo.Postcode;
import clubmanagement.ports.DeliveryTowns;

import java.util.List;

/** Delivery towns read from La Poste's official postcodes (see resources/laposte/README.md). */
public class LaPosteDeliveryTowns implements DeliveryTowns {
    public static LaPosteDeliveryTowns fromOfficialPostcodes() {
        return new LaPosteDeliveryTowns();
    }

    @Override
    public List<String> servedBy(Postcode postcode) {
        return List.of();
    }
}
