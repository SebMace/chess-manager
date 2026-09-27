package clubmanagement.townsofpostcode;

import clubmanagement.domain.club.vo.Postcode;
import clubmanagement.ports.DeliveryTowns;

import java.util.List;

/** The towns an administrator can choose for a postal address, once its postcode is known. */
public class TownsOfPostcode {
    private final DeliveryTowns deliveryTowns;

    public TownsOfPostcode(DeliveryTowns deliveryTowns) {
        this.deliveryTowns = deliveryTowns;
    }

    public List<String> execute(Postcode postcode) {
        return deliveryTowns.servedBy(postcode);
    }
}
