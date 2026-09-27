package clubmanagement.townsofpostcode.rest;

import clubmanagement.domain.club.vo.Postcode;
import clubmanagement.townsofpostcode.TownsOfPostcode;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class TownsOfPostcodeController {
    private final TownsOfPostcode townsOfPostcode;

    public TownsOfPostcodeController(TownsOfPostcode townsOfPostcode) {
        this.townsOfPostcode = townsOfPostcode;
    }

    @GetMapping("/towns")
    public List<String> ofPostcode(@RequestParam String postcode) {
        return townsOfPostcode.execute(new Postcode(postcode));
    }
}
