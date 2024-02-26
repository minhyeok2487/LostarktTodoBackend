package lostark.todo.domain.character.goldCheckPolicy;

import lostark.todo.domain.character.goldCheckPolicy.GoldCheckPolicy;
import lostark.todo.domain.character.goldCheckPolicy.RaidCheckPolicy;
import lostark.todo.domain.character.goldCheckPolicy.TopThreePolicy;

public enum GoldCheckPolicyEnum {

    RAID_CHECK_POLICY(new RaidCheckPolicy()),

    TOP_THREE_POLICY(new TopThreePolicy());

    private final GoldCheckPolicy policy;

    GoldCheckPolicyEnum(GoldCheckPolicy policy) {
        this.policy = policy;
    }

    public GoldCheckPolicy getPolicy() {
        return policy;
    }
}
