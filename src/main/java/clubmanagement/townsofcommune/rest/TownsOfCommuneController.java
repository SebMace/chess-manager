package clubmanagement.townsofcommune.rest;

import clubmanagement.domain.club.vo.Postcode;
import clubmanagement.domain.commune.CommuneCode;
import clubmanagement.townsofcommune.TownsOfCommune;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class TownsOfCommuneController {
    private final TownsOfCommune townsOfCommune;

    public TownsOfCommuneController(TownsOfCommune townsOfCommune) {
        this.townsOfCommune = townsOfCommune;
    }

    @GetMapping(value = "/towns", params = "commune")
    public List<TownResponse> ofCommune(@RequestParam String commune) {
        return townsOfCommune.execute(new CommuneCode(commune)).stream()
                .map(town -> new TownResponse(town.name(), town.postcodes().stream().map(Postcode::value).toList()))
                .toList();
    }

    public record TownResponse(String name, List<String> postcodes) {
    }
}
