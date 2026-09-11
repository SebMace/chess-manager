package domain.member.vo;

public record FfeLicense(FfeId ffeId, FfeLicenseType type) {
    public FfeLicense {
        if (ffeId == null) throw new IllegalArgumentException("ffeId cannot be null");
        if (type == null) throw new IllegalArgumentException("licenseType cannot be null");
    }
}
