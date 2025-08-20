package lostark.todo.domain.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lostark.todo.domain.content.enums.Category;
import lostark.todo.domain.content.enums.WeekContentCategory;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddContentRequest {
    private String contentType; // "day", "week", "cube"

    // Common fields
    private String name;
    private double level;
    private Category category;

    // DayContent fields
    private double shilling;
    private double honorShard;
    private double leapStone;
    private double destructionStone;
    private double guardianStone;
    private double jewelry;

    // WeekContent fields
    private String weekCategory;
    private WeekContentCategory weekContentCategory;
    private int gate;
    private int gold;
    private int characterGold;
    private int coolTime;
    private int moreRewardGold;

    // CubeContent fields
    private double solarGrace;
    private double solarBlessing;
    private double solarProtection;
    private double cardExp;
    private double lavasBreath;
    private double glaciersBreath;
}
