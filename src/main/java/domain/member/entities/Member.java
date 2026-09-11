package domain.member.entities;

import domain.club.vo.ClubId;
import domain.club.vo.Season;
import domain.exceptions.FideIdAlreadyAssignedException;
import domain.member.vo.EloRating;
import domain.member.vo.FideId;
import domain.member.vo.FfeId;
import domain.member.vo.FfeLicense;
import domain.member.vo.FfeLicenseType;
import domain.member.vo.MemberId;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class Member {

    private final MemberId memberId;
    private FideId fideId;
    private FfeLicense ffeLicense;
    private final String firstName;
    private final String lastName;

    private EloRating eloRating;
    private EloRating eloRatingLastRecorded;

    private final Map<Season, ClubId> seasonsClub;

    public Member(MemberId memberId, String firstName, String lastName) {
        if (memberId == null) throw new IllegalArgumentException("memberId cannot be null");
        this.memberId = memberId;
        this.firstName = firstName;
        this.lastName = lastName;
        seasonsClub = new HashMap<>();
    }

    public String firstName() {
        return firstName;
    }
    public String lastName() {
        return lastName;
    }

    public void giveEloRating(EloRating eloRating) {
        this.eloRating = eloRating;
    }

    public void recordEloRating(EloRating eloRating) {
        this.eloRatingLastRecorded = this.eloRating;
        this.eloRating = eloRating;
    }

    public EloRating eloRating() {
    return this.eloRating;
    }

    public void registerFideId(FideId fideId) throws FideIdAlreadyAssignedException {
        if (this.fideId != null) throw new FideIdAlreadyAssignedException();
        this.fideId = fideId;
    }

    public Optional<FideId> fideId() {return Optional.ofNullable(fideId);}

    public Optional<FfeId> ffeId() {
        return Optional.ofNullable(ffeLicense).map(FfeLicense::ffeId);
    }

    public Optional<FfeLicenseType> ffeLicenseType() {
        return Optional.ofNullable(ffeLicense).map(FfeLicense::type);
    }

    public void registerFfeLicense(FfeId ffeId, FfeLicenseType licenseType) {
        this.ffeLicense = new FfeLicense(ffeId, licenseType);
    }

    public MemberId id() {return memberId;}
    public void affiliateTo(ClubId clubId, Season season) {
        if (season==null) throw new IllegalArgumentException("season cannot be null");
        if (clubId == null) throw new IllegalArgumentException("clubId cannot be null");
        if (club(season).filter(currentClub -> currentClub.equals(clubId)).isPresent()) return;
        if (seasonsClub.containsKey(season)) throw new IllegalStateException("Member already affiliated for this season");
        seasonsClub.put(season, clubId);

    }

    public Optional<ClubId> club(Season season) {
        return Optional.ofNullable(seasonsClub.get(season));
    }
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Member member = (Member) o;
        return Objects.equals(memberId, member.memberId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(memberId);
    }
}
