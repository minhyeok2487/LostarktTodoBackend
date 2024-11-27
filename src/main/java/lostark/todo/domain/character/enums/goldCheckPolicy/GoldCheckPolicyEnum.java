package lostark.todo.domain.character.enums.goldCheckPolicy;

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
