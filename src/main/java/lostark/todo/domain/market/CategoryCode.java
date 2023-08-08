package lostark.todo.domain.market;

public enum CategoryCode {

    재련재료(50000),
    테스트용(111111111);

    private final int value;

    CategoryCode(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
