package domain.member.vo;

import java.io.Serializable;
import java.util.UUID;

public record MemberId(UUID memberId) implements Serializable {

    public MemberId(UUID memberId) {
        if (memberId == null) throw new IllegalArgumentException("memberId cannot be null");
        this.memberId = memberId;
    }
}
