package clubmanagement.postcodesoftown.rest;

import clubmanagement.domain.club.vo.Postcode;
import clubmanagement.postcodesoftown.PostcodesOfTown;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class PostcodesOfTownController {
    private final PostcodesOfTown postcodesOfTown;

    public PostcodesOfTownController(PostcodesOfTown postcodesOfTown) {
        this.postcodesOfTown = postcodesOfTown;
    }

    @GetMapping("/postcodes")
    public List<String> ofTown(@RequestParam String town) {
        return postcodesOfTown.execute(town).stream().map(Postcode::value).toList();
    }
}
