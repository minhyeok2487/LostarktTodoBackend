package lostark.todo.domain.todo;

import lostark.todo.domain.content.Category;


public enum TodoContentName {
    발탄_노말("발탄(노말)", "발탄", 2, 1),
    발탄_하드("발탄(하드)", "발탄", 2, 2),

    비아키스_노말("비아키스(노말)", "비아키스", 3, 3),
    비아키스_하드("비아키스(하드)", "비아키스", 3, 4),

    쿠크세이튼_노말("쿠크세이튼(노말)", "쿠크세이튼", 3, 5),

    아브렐슈드_노말_12("아브렐슈드(노말) 1~2", "아브렐슈드", 2, 6),
    아브렐슈드_노말_14("아브렐슈드(노말) 1~4", "아브렐슈드", 4, 7),
    아브렐슈드_노말_16("아브렐슈드(노말) 1~6", "아브렐슈드", 6, 8),
    아브렐슈드_하드_12("아브렐슈드(하드) 1~2", "아브렐슈드", 2, 9),
//    아브렐슈드_하12_노34("아브렐슈드(하12 노34)", "아브렐슈드", 1540),
    아브렐슈드_하드_14("아브렐슈드(하드) 1~4", "아브렐슈드", 4, 10),
    아브렐슈드_하드_16("아브렐슈드(하드) 1~6", "아브렐슈드", 6, 11),

    카양겔_노말("카양겔(노말)", "카양겔", 4, 12),
    카양겔_하드("카양겔(하드)", "카양겔", 4, 13),

    일리아칸_노말("일리아칸(노말)", "일리아칸", 3, 14),
//    일리아칸_하12_노3("일리아칸(하12 노3)", "일리아칸", 1600),
    일리아칸_하드("일리아칸(하드)", "일리아칸", 3, 15),

    상아탑_노말("상아탑(노말)", "상아탑", 4, 16),
    상아탑_하드("상아탑(하드)", "상아탑", 4, 17);

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
