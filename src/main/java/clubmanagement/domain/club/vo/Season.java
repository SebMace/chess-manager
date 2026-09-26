package clubmanagement.domain.club.vo;

public record Season(int yearBegin, int yearEnd) {
    public Season(int yearBegin, int yearEnd) {
        this.yearBegin = yearBegin;
        this.yearEnd = yearEnd;
    }
}
