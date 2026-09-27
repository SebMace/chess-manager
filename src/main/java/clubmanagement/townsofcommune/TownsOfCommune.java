package clubmanagement.townsofcommune;

import clubmanagement.domain.club.vo.DeliveryTown;
import clubmanagement.domain.commune.CommuneCode;
import clubmanagement.ports.DeliveryTowns;

import java.util.List;

/** The towns, and their postcodes, an administrator is offered for the postal addresses of a commune. */
public class TownsOfCommune {
    private final DeliveryTowns deliveryTowns;

    public TownsOfCommune(DeliveryTowns deliveryTowns) {
        this.deliveryTowns = deliveryTowns;
    }

    public List<DeliveryTown> execute(CommuneCode commune) {
        return List.of();
    }
}
