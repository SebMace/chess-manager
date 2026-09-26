package clubmanagement.createclub;

import clubmanagement.domain.club.vo.FfeClubId;

public class FfeClubIdAlreadyUsed extends RuntimeException {
    private final FfeClubId ffeClubId;

    public FfeClubIdAlreadyUsed(FfeClubId ffeClubId) {
        super("The FFE identifier " + ffeClubId.value() + " is already used by another club");
        this.ffeClubId = ffeClubId;
    }

    public FfeClubId ffeClubId() { return ffeClubId; }
}
