package clubmanagement.postcodesoftown;

import clubmanagement.domain.club.vo.Postcode;
import clubmanagement.ports.DeliveryTowns;

import java.util.List;

/** The postcodes an administrator can choose for a postal address, once its town is known. */
public class PostcodesOfTown {
    private final DeliveryTowns deliveryTowns;

    public PostcodesOfTown(DeliveryTowns deliveryTowns) {
        this.deliveryTowns = deliveryTowns;
    }

    public List<Postcode> execute(String town) {
        return List.of();
    }
}
