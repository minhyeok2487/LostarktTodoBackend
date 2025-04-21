package lostark.todo.domain.market.enums;

public enum CategoryCode {

    재련재료(50000),
    보석(210000),
    테스트용(111111111);

    private final int value;

    CategoryCode(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
