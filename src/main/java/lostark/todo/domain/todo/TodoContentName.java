package lostark.todo.domain.todo;

import lostark.todo.domain.content.Category;


public enum TodoContentName {
    발탄_노말("발탄(노말)", "발탄", 2, 1),
    발탄_하드("발탄(하드)", "발탄", 2, 2),

    비아키스_노말("비아키스(노말)", "비아키스", 3, 3),
    비아키스_하드("비아키스(하드)", "비아키스", 3, 4),

    쿠크세이튼_노말("쿠크세이튼(노말)", "쿠크세이튼", 3, 5),

    아브렐슈드_노말_13("아브렐슈드(노말) 1~3", "아브렐슈드", 3, 6),
    아브렐슈드_노말_14("아브렐슈드(노말) 1~4(2주)", "아브렐슈드", 4, 7),
    아브렐슈드_하드_13("아브렐슈드(하드) 1~3", "아브렐슈드", 3, 8),
    아브렐슈드_하드_14("아브렐슈드(하드) 1~4(2주)", "아브렐슈드", 4, 9),

    카양겔_노말("카양겔(노말)", "카양겔", 3, 10),
    카양겔_하드("카양겔(하드)", "카양겔", 3, 11),

    일리아칸_노말("일리아칸(노말)", "일리아칸", 3, 12),
    일리아칸_하드("일리아칸(하드)", "일리아칸", 3, 13),

    상아탑_노말("상아탑(노말)", "상아탑", 4, 14),
    상아탑_하드("상아탑(하드)", "상아탑", 4, 15),

    카멘_노말("카멘(노말)", "카멘", 3, 16),
    카멘_하드_13("카멘(하드) 1~3", "카멘", 3, 17),
    카멘_하드_14("카멘(하드) 1~4(2주)", "카멘", 4, 18);

    private final String displayName;

    private final String category;

    private final int gate;

    private final int sort;

    TodoContentName(String displayName, String category, int gate, int sort) {
        this.displayName = displayName;
        this.category = category;
        this.gate = gate;
        this.sort = sort;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getCategory() {
        return category;
    }

    public int getGate() {
        return gate;
    }

    public int getSort() {
        return sort;
    }
}
