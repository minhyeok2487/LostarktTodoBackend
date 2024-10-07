package lostark.todo.domainV2.character.enums.goldCheckPolicy;

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
